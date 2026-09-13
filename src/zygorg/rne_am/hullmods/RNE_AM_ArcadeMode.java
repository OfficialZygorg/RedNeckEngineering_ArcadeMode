package zygorg.rne_am.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import java.util.Map;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
public class RNE_AM_ArcadeMode extends BaseHullMod implements LunaSettingsListener {
  public static String MOD_ID = "zzzzRNE_AM";
  public static String PPT_DURATION_TYPE = LunaSettings.getString(MOD_ID, "pptDurationType");
  public static String CR_LOSS_TYPE = LunaSettings.getString(MOD_ID, "crLossType");
  public static String SPM_TYPE = LunaSettings.getString(MOD_ID, "suppliesPerMonthType");
  public static String FU_TYPE = LunaSettings.getString(MOD_ID, "fuelUsageType");
  public static String STR_TYPE = LunaSettings.getString(MOD_ID, "suppliesToRecoverType");
  public static String STORAGE_TYPE = LunaSettings.getString(MOD_ID, "storageType");
  public static String FUEL_TYPE = LunaSettings.getString(MOD_ID, "fuelType");
  public static String CREW_TYPE = LunaSettings.getString(MOD_ID, "crewType");
  public static String SALVAGE_TYPE = LunaSettings.getString(MOD_ID, "salvageType");
  public static int PPT_DURATION = LunaSettings.getInt(MOD_ID, "pptDuration"); //Modifies the PPT time of ships
  public static int CR_LOSS = LunaSettings.getInt(MOD_ID, "crLoss"); // Modifies the CR Loss per second of ships
  public static int SPM = LunaSettings.getInt(MOD_ID, "suppliesPerMonth"); // Modifies the suplies per month of ships
  public static int FU = LunaSettings.getInt(MOD_ID, "fuelUsage"); // Modifies the fuel usage per month of ships
  public static int STR = LunaSettings.getInt(MOD_ID, "suppliesToRecover"); // Modifies the supplies to recover a ship after combat (how many supplies will a ship use after combat)
  public static int STORAGE = LunaSettings.getInt(MOD_ID, "storage"); // Modifies the storage capacity to the ship
  public static int FUEL = LunaSettings.getInt(MOD_ID, "fuel"); // Modifies the fuel capacity to the ship
  public static int CREW = LunaSettings.getInt(MOD_ID, "crew"); // Modifies the crew capacity to the ship
  public static int SALVAGE = LunaSettings.getInt(MOD_ID, "salvage"); // Modifies the bonus salvage
  public static Map<String, String> TYPES = Map.of(
          "Add", "+",
          "Subtract", "-",
          "Multiply", "x",
          "Divide", "/"
  );
  @Override
  public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    modifyStat(stats.getPeakCRDuration(), id, PPT_DURATION, PPT_DURATION_TYPE);
    modifyStat(stats.getCRLossPerSecondPercent(), id, CR_LOSS, CR_LOSS_TYPE);
    modifyStat(stats.getSuppliesPerMonth(), id, SPM, SPM_TYPE);
    modifyStat(stats.getFuelUseMod(), id, FU, FU_TYPE);
    modifyStat(stats.getSuppliesToRecover(), id, STR, STR_TYPE);
    modifyStat(stats.getCargoMod(), id, STORAGE, STORAGE_TYPE);
    modifyStat(stats.getFuelMod(), id, FUEL, FUEL_TYPE);
    modifyStat(stats.getMaxCrewMod(), id, CREW, CREW_TYPE);
    modifyStat(stats.getDynamic().getMod(Stats.SALVAGE_VALUE_MULT_MOD), id, SALVAGE, SALVAGE_TYPE);
  }
  @Override
  public void settingsChanged(String modID) throws NullPointerException {
    PPT_DURATION_TYPE = LunaSettings.getString(modID, "pptDurationType");
    CR_LOSS_TYPE = LunaSettings.getString(modID, "crLossType");
    SPM_TYPE = LunaSettings.getString(modID, "suppliesPerMonthType");
    FU_TYPE = LunaSettings.getString(modID, "fuelUsageType");
    STR_TYPE = LunaSettings.getString(modID, "suppliesToRecoverType");
    STORAGE_TYPE = LunaSettings.getString(modID, "storageType");
    FUEL_TYPE = LunaSettings.getString(modID, "fuelType");
    CREW_TYPE = LunaSettings.getString(modID, "crewType");
    SALVAGE_TYPE = LunaSettings.getString(MOD_ID, "salvageType");
    PPT_DURATION = LunaSettings.getInt(modID, "pptDuration");
    CR_LOSS = LunaSettings.getInt(modID, "crLoss");
    SPM = LunaSettings.getInt(modID, "suppliesPerMonth");
    FU = LunaSettings.getInt(modID, "fuelUsage");
    STR = LunaSettings.getInt(modID, "suppliesToRecover");
    STORAGE = LunaSettings.getInt(modID, "storage");
    FUEL = LunaSettings.getInt(modID, "fuel");
    CREW = LunaSettings.getInt(modID, "crew");
    SALVAGE = LunaSettings.getInt(MOD_ID, "salvage");
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
    if (index == 8) return getType(SALVAGE_TYPE) + SALVAGE;
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