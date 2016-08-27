

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.item.ItemManager;
import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.files.HullConfigManager;
import org.burntgameproductions.PathToMani.game.GameColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetConfigs {
  private final Map<String, PlanetConfig> myAllConfigs;
  private final List<PlanetConfig> myEasy;
  private final List<PlanetConfig> myMedium;
  private final List<PlanetConfig> myHard;

  public PlanetConfigs(TextureManager textureManager, HullConfigManager hullConfigs, GameColors cols, ItemManager itemManager) {
    myAllConfigs = new HashMap<String, PlanetConfig>();
    myEasy = new ArrayList<PlanetConfig>();
    myMedium = new ArrayList<PlanetConfig>();
    myHard = new ArrayList<PlanetConfig>();

    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("planets.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      PlanetConfig c = PlanetConfig.load(textureManager, hullConfigs, configFile, sh, cols, itemManager);
      myAllConfigs.put(sh.name, c);
      if (c.hardOnly) myHard.add(c);
      else if (c.easyOnly) myEasy.add(c);
      else myMedium.add(c);
    }
  }

  public PlanetConfig getConfig(String name) {
    return myAllConfigs.get(name);
  }

  public PlanetConfig getRandom(boolean easy, boolean hard) {
    List<PlanetConfig> cfg = easy ? myEasy : hard ? myHard : myMedium;
    return ManiMath.elemRnd(cfg);
  }

  public Map<String, PlanetConfig> getAllConfigs() {
    return myAllConfigs;
  }
}
