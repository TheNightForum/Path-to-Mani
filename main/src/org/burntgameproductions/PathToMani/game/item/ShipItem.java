

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

public class ShipItem implements ManiItem {

  public static final ManiItemType EMPTY = new ManiItemType(new Color(), null, 1);
  private final HullConfig myConfig;
  private final String myDesc;

  public ShipItem(HullConfig config) {
    myConfig = config;
    myDesc = makeDesc(myConfig);
  }

  public static String makeDesc(HullConfig hull) {
    StringBuilder sb = new StringBuilder();
    sb.append("Takes ").append(hull.getMaxLife()).append(" dmg\n");
    boolean secondGunSlot = hull.getNrOfGunSlots() > 1;
    if (!secondGunSlot || hull.getGunSlot(0).allowsRotation() != hull.getGunSlot(1).allowsRotation()) {
      if (!secondGunSlot) {
        sb.append(!hull.getGunSlot(0).allowsRotation() ? "1 heavy gun slot\n" : "1 light gun slot\n");
      } else {
        sb.append("1 heavy + 1 light gun slots\n");
      }
    } else {
      sb.append(!hull.getGunSlot(0).allowsRotation() ? "2 heavy gun slots\n" : "2 light gun slots\n");
    }
    if (hull.getAbility() != null) {
      sb.append("Ability:\n");
      hull.getAbility().appendDesc(sb);
    }
    return sb.toString();
  }

  @Override
  public String getDisplayName() {
    return myConfig.getDisplayName();
  }

  @Override
  public float getPrice() {
    return myConfig.getPrice();
  }

  @Override
  public String getDesc() {
    return myDesc;
  }

  @Override
  public ManiItem copy() {
    return new ShipItem(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof ShipItem && ((ShipItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return myConfig.getIcon();
  }

  @Override
  public ManiItemType getItemType() {
    return EMPTY;
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

  public HullConfig getConfig() {
    return myConfig;
  }
}
