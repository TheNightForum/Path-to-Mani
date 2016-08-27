

package org.burntgameproductions.PathToMani.game.item;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class RepairItem implements ManiItem {
  public static final int LIFE_AMT = 20;
  private final ManiItemType myItemType;

  public RepairItem(ManiItemType itemType) {
    myItemType = itemType;
  }

  @Override
  public String getDisplayName() {
    return "Repair Kit";
  }

  @Override
  public float getPrice() {
    return 30;
  }

  @Override
  public String getDesc() {
    return "Stay idle to fix " + LIFE_AMT + " dmg";
  }

  @Override
  public ManiItem copy() {
    return new RepairItem(myItemType);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof RepairItem;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return game.getItemMan().repairIcon;
  }

  @Override
  public ManiItemType getItemType() {
    return myItemType;
  }

  @Override
  public String getCode() {
    return "rep";
  }

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }
}
