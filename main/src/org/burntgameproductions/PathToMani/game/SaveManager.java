

package org.burntgameproductions.PathToMani.game;

import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.files.HullConfigManager;
import org.burntgameproductions.PathToMani.game.item.ItemManager;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;
import org.burntgameproductions.PathToMani.IniReader;
import org.burntgameproductions.PathToMani.game.gun.GunItem;

import java.util.ArrayList;

public class SaveManager {

  public static final String FILE_NAME = "prevShip.ini";

  public static void writeShip(HullConfig hull, float money, ArrayList<ManiItem> items, ManiGame game) {
    String hullName = game.getHullConfigs().getName(hull);
    StringBuilder sb = new StringBuilder();
    for (ManiItem i : items) {
      sb.append(i.getCode());
      if (i.isEquipped() > 0) {
        sb.append("-" + i.isEquipped());
      }
      sb.append(" ");
      // Save gun's loaded ammo
      if (i instanceof GunItem) {
        GunItem g = (GunItem) i;
        if (g.ammo > 0 && !g.config.clipConf.infinite) {
          sb.append(g.config.clipConf.code + " ");
        }
      }
    }
    IniReader.write(FILE_NAME, "hull", hullName, "money", (int) money, "items", sb.toString());
  }

  public static boolean hasPrevShip() {
    return FileManager.getInstance().getDynamicFile(FILE_NAME).exists();
  }

  public static ShipConfig readShip(HullConfigManager hullConfigs, ItemManager itemManager) {
    IniReader ir = new IniReader(FILE_NAME, null, false);
    String hullName = ir.getString("hull", null);
    if (hullName == null) return null;
    HullConfig hull = hullConfigs.getConfig(hullName);
    if (hull == null) return null;
    int money = ir.getInt("money", 0);
    String itemsStr = ir.getString("items", "");
    return new ShipConfig(hull, itemsStr, money, 1, null, itemManager);
  }
}
