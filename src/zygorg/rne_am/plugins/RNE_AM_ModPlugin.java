package zygorg.rne_am.plugins;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import lunalib.lunaSettings.LunaSettingsListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import lunalib.lunaSettings.LunaSettings;
import zygorg.rne_am.hullmods.RNE_AM_ArcadeMode;
public class RNE_AM_ModPlugin extends BaseModPlugin {
  private static final Logger log = Logger.getLogger(RNE_AM_ModPlugin.class);
  public RNE_AM_ModPlugin() {
  }
  @Override
  public void onApplicationLoad() throws Exception { //Thanks a lot ruddygreat (USC)
    LunaSettings.addSettingsListener(new RNE_AM_ArcadeMode());
    for (ShipHullSpecAPI spec : Global.getSettings().getAllShipHullSpecs()) {
      spec.addBuiltInMod("RNE_AM_ArcadeMode");
      log.log(Level.INFO, "(Hull) Adding hullmod RNE_AM_Arcade to: " + spec.getHullId());
    }
    
    for (String varId : Global.getSettings().getAllVariantIds()) {
      var variant = Global.getSettings().getVariant(varId);
      if (variant != null) {
        variant.addPermaMod("RNE_AM_ArcadeMode");
        variant.addMod("RNE_AM_ArcadeMode");
        log.log(Level.INFO, "(HullVariant) Adding hullmod RNE_AM_Arcade to: " + variant.getHullVariantId());
      }
    }
  }
}