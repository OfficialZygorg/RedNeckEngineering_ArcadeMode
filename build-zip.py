import json
import shutil
import subprocess
import tempfile
import re
from pathlib import Path

# ============================================================
# Configuration
# ============================================================

PROJECT_DIR = Path(__file__).resolve().parent
MOD_INFO_FILE = PROJECT_DIR / "mod_info.json"
JAR_FILE = PROJECT_DIR / "jars" / "RNE_AM.jar"
OUTPUT_DIRECTORY = PROJECT_DIR / "zips"
MOD_VERSION_FILE = PROJECT_DIR / "RNE_AM.version"

# Files that should NOT be included in the release ZIP.
EXCLUDED_PATHS = {
    ".gitignore",
    ".iml",
    "build-zip.py",
}

# Directories that should NOT be included in the release ZIP.
EXCLUDED_DIRECTORIES = {
    ".idea",
    "src",
}


# ============================================================
# Helpers
# ============================================================

def get_version():
    """Read the version from mod_info.json."""
    with MOD_INFO_FILE.open("r", encoding="utf-8") as file:
        mod_info = json.load(file)

    version = mod_info["version"]

    return (
        f"{version['major']}."
        f"{version['minor']}."
        f"{version['patch']}"
    )


def get_git_files():
    """Get files tracked by Git."""
    result = subprocess.run(
        ["git", "ls-files", "-z"],
        cwd=PROJECT_DIR,
        capture_output=True,
        check=True,
    )

    return [
        Path(path)
        for path in result.stdout.decode("utf-8").split("\0")
        if path
    ]


def should_exclude(path):
    """Return True if a file should not be included."""
    if path.name in EXCLUDED_PATHS:
        return True

    if any(
        directory in EXCLUDED_DIRECTORIES
        for directory in path.parts
    ):
        return True

    return False


def copy_file(source, destination):
    """Copy a file while creating its parent directory."""
    destination.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(source, destination)

def sync_mod_version():
    """Synchronize modVersion in RNE_AM.version with mod_info.json."""
    with MOD_INFO_FILE.open("r", encoding="utf-8") as file:
        mod_info = json.load(file)

    version = mod_info["version"]

    major = version["major"]
    minor = version["minor"]
    patch = version["patch"]

    with MOD_VERSION_FILE.open("r", encoding="utf-8") as file:
        mod_version_content = file.read()

    pattern = (
        r'("modVersion"\s*:\s*\{'
        r'.*?"major"\s*:\s*)'
        r'([^,\s]+)'
        r'(\s*,\s*"minor"\s*:\s*)'
        r'([^,\s]+)'
        r'(\s*,\s*"patch"\s*:\s*)'
        r'([^#,\s]+)'
        r'(\s*#.*)'
    )

    replacement = (
        rf'\g<1>{major}'
        rf'\g<3>{minor}'
        rf'\g<5>{patch}'
        rf'\g<7>'
    )

    updated_content, replacements = re.subn(
        pattern,
        replacement,
        mod_version_content,
        count=1,
    )

    if replacements != 1:
        raise RuntimeError(
            "Could not find the modVersion block in RNE_AM.version."
        )

    MOD_VERSION_FILE.write_text(
        updated_content,
        encoding="utf-8",
    )

def format_version_value(value):
    """Format a version value for RNE_AM.version."""
    if isinstance(value, str):
        return json.dumps(value)

    return str(value)


# ============================================================
# Main
# ============================================================

def main():
    print("========================================")
    print(" Red Neck Engineering - Build ZIP")
    print("========================================")
    print()

    try:
        # ----------------------------------------------------
        # Check required files
        # ----------------------------------------------------

        if not MOD_INFO_FILE.exists():
            raise FileNotFoundError(
                f"Could not find {MOD_INFO_FILE}"
            )

        if not MOD_VERSION_FILE.exists():
            raise FileNotFoundError(
                f"Could not find {MOD_VERSION_FILE}"
            )

        if not JAR_FILE.exists():
            raise FileNotFoundError(
                "\n"
                "Could not find the compiled JAR:\n"
                f"  {JAR_FILE}\n\n"
                "Build the project in IntelliJ IDEA first."
            )

        # ----------------------------------------------------
        # Read project information
        # ----------------------------------------------------

        version = get_version()

        print("Synchronizing RNE_AM.version...")
        sync_mod_version()

        project_name = PROJECT_DIR.name

        OUTPUT_DIRECTORY.mkdir(parents=True, exist_ok=True)

        output_file = (
            OUTPUT_DIRECTORY / f"{project_name}-{version}.zip"
        )

        print(f"Project : {project_name}")
        print(f"Version : {version}")
        print(f"JAR     : {JAR_FILE}")
        print(f"Output  : {output_file}")
        print()

        # ----------------------------------------------------
        # Get Git-tracked files
        # ----------------------------------------------------

        git_files = get_git_files()

        git_files = [
            path
            for path in git_files
            if not should_exclude(path)
        ]

        print(f"Files to package: {len(git_files)}")
        print()

        # ----------------------------------------------------
        # Create temporary staging directory
        # ----------------------------------------------------

        with tempfile.TemporaryDirectory(
            prefix="rne_am_build_"
        ) as temp_directory:

            temp_directory = Path(temp_directory)

            staging_directory = (
                temp_directory / project_name
            )

            staging_directory.mkdir()

            print("Copying project files...")

            for relative_file in git_files:
                source = PROJECT_DIR / relative_file

                if not source.is_file():
                    continue

                destination = (
                    staging_directory / relative_file
                )

                copy_file(source, destination)

            # ------------------------------------------------
            # Copy freshly compiled JAR
            # ------------------------------------------------

            print("Copying freshly compiled JAR...")

            destination_jar = (
                staging_directory
                / "jars"
                / "RNE_AM.jar"
            )

            copy_file(
                JAR_FILE,
                destination_jar
            )

            # ------------------------------------------------
            # Remove previous ZIP
            # ------------------------------------------------

            if output_file.exists():
                print("Removing previous ZIP...")
                output_file.unlink()

            # ------------------------------------------------
            # Create ZIP
            # ------------------------------------------------

            print("Creating ZIP...")

            shutil.make_archive(
                str(output_file.with_suffix("")),
                "zip",
                root_dir=temp_directory,
                base_dir=project_name,
            )

        # ----------------------------------------------------
        # Finished
        # ----------------------------------------------------

        print()
        print("========================================")
        print(" Build completed successfully!")
        print("========================================")
        print()
        print(f"Created:")
        print(f"  {output_file}")
        print()

    except Exception as error:
        print()
        print("========================================")
        print(" BUILD FAILED")
        print("========================================")
        print()
        print(error)
        print()


# ============================================================
# Run
# ============================================================

if __name__ == "__main__":
    main()