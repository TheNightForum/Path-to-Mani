

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.ShipConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class MercItem implements ManiItem {
  private final ShipConfig myConfig;
  private final String myDesc;

  public MercItem(ShipConfig config) {
    myConfig = config;
    myDesc = "Has a shield and repairers\n" + ShipItem.makeDesc(myConfig.hull);
  }

  @Override
  public String getDisplayName() {
    return myConfig.hull.getDisplayName();
  }

  @Override
  public float getPrice() {
    return myConfig.hull.getHirePrice();
  }

  @Override
  public String getDesc() {
    return myDesc;
  }

  @Override
  public ManiItem copy() {
    return new MercItem(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof MercItem && ((MercItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return myConfig.hull.getIcon();
  }

  @Override
  public ManiItemType getItemType() {
    return ShipItem.EMPTY;
  }

  @Override
  public String getCode() {
    return null;
  }

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }

  public ShipConfig getConfig() {
    return myConfig;
  }
}
