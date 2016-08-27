

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class ClipItem implements ManiItem {
  private final ClipConfig myConfig;

  public ClipItem(ClipConfig config) {
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

  public ClipConfig getConfig() {
    return myConfig;
  }

  @Override
  public ManiItem copy() {
    return new ClipItem(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof ClipItem && ((ClipItem) item).myConfig == myConfig;
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
}
