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

    major = format_version_value(version["major"])
    minor = format_version_value(version["minor"])
    patch = format_version_value(version["patch"])

    with MOD_VERSION_FILE.open("r", encoding="utf-8") as file:
        content = file.read()

    # Find the modVersion block only.
    mod_version_pattern = re.compile(
        r'("modVersion"\s*:\s*\{)(.*?)(\})',
        re.DOTALL,
    )

    match = mod_version_pattern.search(content)

    if not match:
        raise RuntimeError(
            "Could not find the modVersion block in RNE_AM.version."
        )

    block = match.group(0)

    # Replace only the values.
    block = re.sub(
        r'("major"\s*:\s*)[^,\s]+',
        rf'\g<1>{major}',
        block,
        count=1,
    )

    block = re.sub(
        r'("minor"\s*:\s*)[^,\s]+',
        rf'\g<1>{minor}',
        block,
        count=1,
    )

    # The patch value stops at the #, preserving the comment.
    block = re.sub(
        r'("patch"\s*:\s*)[^#,\s]+',
        rf'\g<1>{patch}',
        block,
        count=1,
    )

    # Put the modified block back into the complete file.
    updated_content = (
            content[:match.start()]
            + block
            + content[match.end():]
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