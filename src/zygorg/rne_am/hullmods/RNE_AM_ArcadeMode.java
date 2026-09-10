package zygorg.rne_am.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Map;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
public class RNE_AM_ArcadeMode extends BaseHullMod implements LunaSettingsListener {
  public static String MOD_ID = "zzzzRNE_AM";
  public static String PPT_DURATION_TYPE = LunaSettings.getString(MOD_ID, "pptDurationType");
  public static float PPT_DURATION = LunaSettings.getFloat(MOD_ID, "pptDuration"); //Modifies the PPT time of ships
  //  public static float CR_LOSS = LunaSettings.getFloat(MOD_ID, "crLoss"); // Modifies the CR Loss per second of ships
  //  public static float SPM = LunaSettings.getFloat(MOD_ID, "suppliesPerMonth"); // Modifies the suplies per month of ships
  //  public static float FU = LunaSettings.getFloat(MOD_ID, "fuelUsage"); // Modifies the fuel usage per month of ships
  //  public static float STR = LunaSettings.getFloat(MOD_ID, "suppliesToRecover"); // Modifies the supplies to recover a ship after combat (how many supplies will a ship use after combat)
  //  public static float STORAGE = LunaSettings.getFloat(MOD_ID, "storage"); // Modifies the storage capacity to the ship
  //  public static float FUEL = LunaSettings.getFloat(MOD_ID, "fuel"); // Modifies the fuel capacity to the ship
  //  public static float CREW = LunaSettings.getFloat(MOD_ID, "crew"); // Modifies the crew capacity to the ship
  public static Map<String, String> TYPES = Map.of(
          "Add", "+",
          "Subtract", "-",
          "Multiply", "x",
          "Divide", "/"
  );
  @Override
  public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    stats.getPeakCRDuration().modifyFlat(id, PPT_DURATION); //PPT Duration
    //    stats.getCRLossPerSecondPercent().modifyMult(id, CR_LOSS); //CR Degradation per second once the PPT reaches 0
    //    stats.getSuppliesPerMonth().modifyMult(id, SPM); //Supplies per month cost of the ship
    //    stats.getFuelUseMod().modifyMult(id, FU); //Fuel usage per light year of the ship
    //    stats.getSuppliesToRecover().modifyMult(id, STR); //Supply usage per fight
    //    stats.getCargoMod().modifyFlat(id, STORAGE); //Cargo capacity of the ship
    //    stats.getFuelMod().modifyFlat(id, FUEL); //Fuel capacity of the ship
    //    stats.getMaxCrewMod().modifyFlat(id, CREW); //Crew capacity of the ship
  }
  @Override
  public void settingsChanged(String modID) throws NullPointerException {
    PPT_DURATION_TYPE = LunaSettings.getString(MOD_ID, "pptDurationType");
    PPT_DURATION = LunaSettings.getFloat(MOD_ID, "pptDuration");
    //    CR_LOSS = LunaSettings.getFloat(MOD_ID, "crLoss");
    //    SPM = LunaSettings.getFloat(MOD_ID, "SPM");
    //    FU = LunaSettings.getFloat(MOD_ID, "fu");
    //    STR = LunaSettings.getFloat(MOD_ID, "str");
    //    STORAGE = LunaSettings.getFloat(MOD_ID, "storage");
    //    FUEL = LunaSettings.getFloat(MOD_ID, "fuel");
    //    CREW = LunaSettings.getFloat(MOD_ID, "crew");
  }
  @Override
  public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
    if (index == 0) return getType(PPT_DURATION_TYPE) + PPT_DURATION;
    //    if (index == 1) return String.valueOf(CR_LOSS);
    //    if (index == 2) return String.valueOf(SPM);
    //    if (index == 3) return String.valueOf(FU);
    //    if (index == 4) return String.valueOf(STR);
    //    if (index == 5) return String.valueOf(STORAGE);
    //    if (index == 6) return String.valueOf(FUEL);
    //    if (index == 7) return String.valueOf(CREW);
    return null;
  }
  private void modifyPPT(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    float duration = PPT_DURATION;
    String type = PPT_DURATION_TYPE;
    if (duration < 0) duration = 0; //Don't allow negative numbers at the start
    switch (type) {
      case "Add":
        stats.getPeakCRDuration().modifyFlat(id, duration);
        break;
      case "Subtract":
        stats.getPeakCRDuration().modifyFlat(id, duration * -1);
        break;
      case "Multiply":
        stats.getPeakCRDuration().modifyMult(id, duration);
        break;
      case "Divide":
        if (duration < 1) duration = 1; //Can't divide by 0
        stats.getPeakCRDuration().modifyMult(id, duration * -1);
        break;
    }
  }
  private String getType(String modeType) {
    return TYPES.getOrDefault(modeType, "Add");
  }
}