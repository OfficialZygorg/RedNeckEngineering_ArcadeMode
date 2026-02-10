package zygorg.rne_am.hullmods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
public class RNE_AM_ArcadeMode extends BaseHullMod implements LunaSettingsListener {
  public static float CR_DURATION = LunaSettings.getFloat("zzzzRNE_AM", "CR_DURATION"); // Anithing above 100k in seconds the game considers it infinite
  public static float CR_LOSS = LunaSettings.getFloat("zzzzRNE_AM", "CR_LOSS"); // Multiply the CR Loss by 0, to have 0 combat readiness loss per second
  public static float SPP_REDUCTION_MULT = LunaSettings.getFloat("zzzzRNE_AM", "SPP_REDUCTION_MULT"); // Lower the suplies per month of ships by 99.99%
  public static float FU_REDUCTION_MULT = LunaSettings.getFloat("zzzzRNE_AM", "FU_REDUCTION_MULT"); // Lower the suplies per month of ships by 99.99%
  public static float STR_REDUCTION_MULT = LunaSettings.getFloat("zzzzRNE_AM", "STR_REDUCTION_MULT"); // Lower the suplies per month of ships by 99.99%
  public static float STORAGE = LunaSettings.getFloat("zzzzRNE_AM", "STORAGE"); // Add an extra 1000 storage capacity to the ship
  public static float FUEL = LunaSettings.getFloat("zzzzRNE_AM", "FUEL"); // Add an extra 1000 fuel capacity to the ship
  public static float CREW = LunaSettings.getFloat("zzzzRNE_AM", "CREW"); // Add an extra 1000 crew capacity to the ship
  @Override
  public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    stats.getPeakCRDuration().modifyFlat(id, CR_DURATION); //Infinite PPT
    stats.getCRLossPerSecondPercent().modifyMult(id, CR_LOSS); //No CR degradation
    stats.getSuppliesPerMonth().modifyMult(id, SPP_REDUCTION_MULT); //Reduced supplies per month maintenance by 99.99%
    stats.getFuelUseMod().modifyMult(id, FU_REDUCTION_MULT); //Reduced fuel usage by 99.99%
    stats.getSuppliesToRecover().modifyMult(id, STR_REDUCTION_MULT); //Reduces supply usage to deploy a ship into combat by 99.99%
    stats.getCargoMod().modifyFlat(id, STORAGE); //Increase cargo capacity by 1000
    stats.getFuelMod().modifyFlat(id, FUEL); //Increase fuel capacity by 1000
    stats.getMaxCrewMod().modifyFlat(id, CREW); //Increase crew capacity by 500
  }
  @Override
  public void settingsChanged(String modID) throws NullPointerException{
    CR_DURATION = LunaSettings.getFloat("zzzzRNE_AM", "CR_DURATION"); // Anithing above 100k in seconds the game considers it infinite
    CR_LOSS = LunaSettings.getFloat("zzzzRNE_AM", "CR_LOSS"); // Multiply the CR Loss by 0, to have 0 combat readiness loss per second
    SPP_REDUCTION_MULT = LunaSettings.getFloat("zzzzRNE_AM", "SPP_REDUCTION_MULT"); // Lower the suplies per month of ships by 99.99%
    FU_REDUCTION_MULT = LunaSettings.getFloat("zzzzRNE_AM", "FU_REDUCTION_MULT"); // Lower the suplies per month of ships by 99.99%
    STR_REDUCTION_MULT = LunaSettings.getFloat("zzzzRNE_AM", "STR_REDUCTION_MULT"); // Lower the suplies per month of ships by 99.99%
    STORAGE = LunaSettings.getFloat("zzzzRNE_AM", "STORAGE"); // Add an extra 1000 storage capacity to the ship
    FUEL = LunaSettings.getFloat("zzzzRNE_AM", "FUEL"); // Add an extra 1000 fuel capacity to the ship
    CREW = LunaSettings.getFloat("zzzzRNE_AM", "CREW"); // Add an extra 1000 crew capacity to the ship
  }
  @Override
  public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
    if (index == 0) return String.valueOf(CR_DURATION);
    if (index == 1) return String.valueOf(CR_LOSS);
    if (index == 2) return String.valueOf(SPP_REDUCTION_MULT);
    if (index == 3) return String.valueOf(FU_REDUCTION_MULT);
    if (index == 4) return String.valueOf(STR_REDUCTION_MULT);
    if (index == 5) return String.valueOf(STORAGE);
    if (index == 6) return String.valueOf(FUEL);
    if (index == 7) return String.valueOf(CREW);
    return null;
  }
}