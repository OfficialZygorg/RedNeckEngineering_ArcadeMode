package zygorg.rne_am.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.StatBonus;
import java.util.Map;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
public class RNE_AM_ArcadeMode extends BaseHullMod implements LunaSettingsListener {
  public static String MOD_ID = "zzzzRNE_AM";
  public static String PPT_DURATION_TYPE = LunaSettings.getString(MOD_ID, "pptDurationType");
  public static int PPT_DURATION = LunaSettings.getInt(MOD_ID, "pptDuration"); //Modifies the PPT time of ships
  public static String CR_LOSS_TYPE = LunaSettings.getString(MOD_ID, "crLossType");
  public static int CR_LOSS = LunaSettings.getInt(MOD_ID, "crLoss"); // Modifies the CR Loss per second of ships
  public static String SPM_TYPE = LunaSettings.getString(MOD_ID, "suppliesPerMonthType");
  public static int SPM = LunaSettings.getInt(MOD_ID, "suppliesPerMonth"); // Modifies the suplies per month of ships
  public static String FU_TYPE = LunaSettings.getString(MOD_ID, "fuelUsageType");
  public static int FU = LunaSettings.getInt(MOD_ID, "fuelUsage"); // Modifies the fuel usage per month of ships
  public static String STR_TYPE = LunaSettings.getString(MOD_ID, "suppliesToRecoverType");
  public static int STR = LunaSettings.getInt(MOD_ID, "suppliesToRecover"); // Modifies the supplies to recover a ship after combat (how many supplies will a ship use after combat)
  public static String STORAGE_TYPE = LunaSettings.getString(MOD_ID, "storageType");
  public static int STORAGE = LunaSettings.getInt(MOD_ID, "storage"); // Modifies the storage capacity to the ship
  public static String FUEL_TYPE = LunaSettings.getString(MOD_ID, "fuelType");
  public static int FUEL = LunaSettings.getInt(MOD_ID, "fuel"); // Modifies the fuel capacity to the ship
  public static String CREW_TYPE = LunaSettings.getString(MOD_ID, "crewType");
  public static int CREW = LunaSettings.getInt(MOD_ID, "crew"); // Modifies the crew capacity to the ship
  public static Map<String, String> TYPES = Map.of(
          "Add", "+",
          "Subtract", "-",
          "Multiply", "x",
          "Divide", "/" //
  );
  @Override
  public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    Map<MutableShipStatsAPI, Object[]> STATS = Map.of(
            (MutableShipStatsAPI) stats.getPeakCRDuration(), new Object[] {PPT_DURATION, PPT_DURATION_TYPE}, //PPT Duration
            (MutableShipStatsAPI) stats.getCRLossPerSecondPercent(), new Object[] {CR_LOSS, CR_LOSS_TYPE}, //CR Degradation per second once the PPT reaches 0
            (MutableShipStatsAPI) stats.getSuppliesPerMonth(), new Object[] {SPM, SPM_TYPE}, //Supplies per month cost of the ship
            (MutableShipStatsAPI) stats.getFuelUseMod(), new Object[] {FU, FU_TYPE}, //Fuel usage per light year of the ship
            (MutableShipStatsAPI) stats.getSuppliesToRecover(), new Object[] {STR, STR_TYPE}, //Supply usage per fight
            (MutableShipStatsAPI) stats.getCargoMod(), new Object[] {STORAGE, STORAGE_TYPE}, //Cargo capacity of the ship
            (MutableShipStatsAPI) stats.getFuelMod(), new Object[] {FUEL, FUEL_TYPE}, //Fuel capacity of the ship
            (MutableShipStatsAPI) stats.getMaxCrewMod(), new Object[] {CREW, CREW_TYPE} //Crew capacity of the ship
    );
    STATS.forEach((stat, values) -> modifyStat(stat, id, (float) values[0], (String) values[1]));
  }
  @Override
  public void settingsChanged(String modID) throws NullPointerException {
    PPT_DURATION_TYPE = LunaSettings.getString(modID, "pptDurationType");
    PPT_DURATION = LunaSettings.getInt(modID, "pptDuration");
    CR_LOSS_TYPE = LunaSettings.getString(modID, "crLossType");
    CR_LOSS = LunaSettings.getInt(modID, "crLoss");
    SPM_TYPE = LunaSettings.getString(modID, "suppliesPerMonthType");
    SPM = LunaSettings.getInt(modID, "suppliesPerMonth");
    FU_TYPE = LunaSettings.getString(modID, "fuelUsageType");
    FU = LunaSettings.getInt(modID, "fuelUsage");
    STR_TYPE = LunaSettings.getString(modID, "suppliesToRecoverType");
    STR = LunaSettings.getInt(modID, "suppliesToRecover");
    STORAGE_TYPE = LunaSettings.getString(modID, "storageType");
    STORAGE = LunaSettings.getInt(modID, "storage");
    FUEL_TYPE = LunaSettings.getString(modID, "fuelType");
    FUEL = LunaSettings.getInt(modID, "fuel");
    CREW_TYPE = LunaSettings.getString(modID, "crewType");
    CREW = LunaSettings.getInt(modID, "crew");
  }
  @Override
  public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
    if (index == 0) return getType(PPT_DURATION_TYPE) + PPT_DURATION;
    if (index == 1) return getType(CR_LOSS_TYPE) + CR_LOSS;
    if (index == 2) return getType(SPM_TYPE) + SPM;
    if (index == 3) return getType(FU_TYPE) + FU;
    if (index == 4) return getType(STR_TYPE) + STR;
    if (index == 5) return getType(STORAGE_TYPE) + STORAGE;
    if (index == 6) return getType(FUEL_TYPE) + FUEL;
    if (index == 7) return getType(CREW_TYPE) + CREW;
    return null;
  }
  private void modifyStat(MutableStat stat, String id, float value, String type) {
    switch (type) {
      case "Add":
        stat.modifyFlat(id, value);
        break;
      case "Subtract":
        stat.modifyFlat(id, -value);
        break;
      case "Multiply":
        if (value == 0f) return; //Don't multiply by 0
        stat.modifyMult(id, value);
        break;
      case "Divide":
        if (value == 0f) return; //Can't divide by 0
        stat.modifyMult(id, 1f / value);
        break;
    }
  }
  private void modifyStat(StatBonus stat, String id, float value, String type) {
    switch (type) {
      case "Add":
        stat.modifyFlat(id, value);
        break;
      case "Subtract":
        stat.modifyFlat(id, -value);
        break;
      case "Multiply":
        if (value == 0f) return; //Don't multiply by 0
        stat.modifyMult(id, value);
        break;
      case "Divide":
        if (value == 0f) return; //Can't divide by 0
        stat.modifyMult(id, 1f / value);
        break;
    }
  }
  private String getType(String modeType) {
    return TYPES.getOrDefault(modeType, "Add");
  }
}