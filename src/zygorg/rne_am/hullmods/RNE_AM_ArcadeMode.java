package zygorg.rne_am.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import java.util.Map;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
public class RNE_AM_ArcadeMode extends BaseHullMod implements LunaSettingsListener {
  public static String PPT_DURATION_TYPE = LunaSettings.getString("RNE_AM", "pptDurationType");
  public static float PPT_DURATION = LunaSettings.getFloat("RNE_AM", "pptDuration"); //Modifies the CR time of the ships
  //  public static float CR_LOSS = LunaSettings.getFloat("RNE_AM", "crLoss"); // Modifies the CR Loss per second
  //  public static float SPM = LunaSettings.getFloat("RNE_AM", "suppliesPerMonth"); // Modifies the suplies per month of ships
  //  public static float FU = LunaSettings.getFloat("RNE_AM", "fuelUsage"); // Modifies the fuel usage per month of ships
  //  public static float STR = LunaSettings.getFloat("RNE_AM", "suppliesToRecover"); // Modifies the supplies to recover a ship after combat
  //  public static float STORAGE = LunaSettings.getFloat("RNE_AM", "storage"); // Modifies the storage capacity to the ship
  //  public static float FUEL = LunaSettings.getFloat("RNE_AM", "fuel"); // Modifies the fuel capacity to the ship
  //  public static float CREW = LunaSettings.getFloat("RNE_AM", "crew"); // Modifies the crew capacity to the ship
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
    PPT_DURATION = LunaSettings.getFloat("RNE_AM", "pptDuration");
    //    CR_LOSS = LunaSettings.getFloat("RNE_AM", "crLoss");
    //    SPM = LunaSettings.getFloat("RNE_AM", "SPM");
    //    FU = LunaSettings.getFloat("RNE_AM", "fu");
    //    STR = LunaSettings.getFloat("RNE_AM", "str");
    //    STORAGE = LunaSettings.getFloat("RNE_AM", "storage");
    //    FUEL = LunaSettings.getFloat("RNE_AM", "fuel");
    //    CREW = LunaSettings.getFloat("RNE_AM", "crew");
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
      case "Subtract":
        stats.getPeakCRDuration().modifyFlat(id, duration * -1);
      case "Multiply":
        stats.getPeakCRDuration().modifyMult(id, duration);
      case "Divide":
        if (duration < 1) duration = 1; //Can't divide by 0
        stats.getPeakCRDuration().modifyMult(id, duration * -1);
    }
  }
  private String getType(String modeType) {
    return TYPES.get(modeType);
  }
}