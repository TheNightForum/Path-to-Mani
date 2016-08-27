

package org.burntgameproductions.PathToMani.game.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.game.item.ItemManager;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.item.ManiItemType;
import org.burntgameproductions.PathToMani.game.item.ManiItemTypes;
import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class AbilityCharge implements ManiItem {
  private final Config myConfig;

  public AbilityCharge(Config config) {
    myConfig = config;
  }

  @Override
  public String getDisplayName() {
    return myConfig.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.price;
  }

  @Override
  public String getDesc() {
    return myConfig.desc;
  }

  @Override
  public ManiItem copy() {
    return new AbilityCharge(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof AbilityCharge && ((AbilityCharge) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return myConfig.icon;
  }

  @Override
  public ManiItemType getItemType() {
    return myConfig.itemType;
  }

  @Override
  public String getCode() {
    return myConfig.code;
  }

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }


  public static class Config {
    private final TextureAtlas.AtlasRegion icon;
    private final float price;
    private final String displayName;
    private final String desc;
    public final ManiItemType itemType;
    public final String code;

    public Config(TextureAtlas.AtlasRegion icon, float price, String displayName, String desc, ManiItemType itemType,
      String code) {
      this.icon = icon;
      this.price = price;
      this.displayName = displayName;
      this.desc = desc;
      this.itemType = itemType;
      this.code = code;
    }

    public static void load(ItemManager itemManager, TextureManager textureManager, ManiItemTypes types) {
      JsonReader r = new JsonReader();
      FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("abilityCharges.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue ammoNode : parsed) {
        String iconName = ammoNode.getString("iconName");
        TextureAtlas.AtlasRegion icon = textureManager.getTex(TextureManager.ICONS_DIR + iconName, configFile);
        float price = ammoNode.getFloat("price");
        String displayName = ammoNode.getString("displayName");
        String desc = ammoNode.getString("desc");
        String code = ammoNode.name;
        Config c = new Config(icon, price, displayName, desc, types.abilityCharge, code);
        AbilityCharge chargeExample = new AbilityCharge(c);
        itemManager.registerItem(chargeExample);
      }
    }
  }
}
