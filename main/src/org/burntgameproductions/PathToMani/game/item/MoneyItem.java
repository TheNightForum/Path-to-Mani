

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class MoneyItem implements ManiItem {
  public static final int AMT = 10;
  public static final int MED_AMT = 3 * AMT;
  public static final int BIG_AMT = 10 * AMT;

  private final float myAmt;
  private final ManiItemType myItemType;

  public MoneyItem(float amt, ManiItemType itemType) {
    myAmt = amt;
    myItemType = itemType;
  }

  @Override
  public String getDisplayName() {
    return "money";
  }

  @Override
  public float getPrice() {
    return myAmt;
  }

  @Override
  public String getDesc() {
    return "money";
  }

  @Override
  public MoneyItem copy() {
    return new MoneyItem(myAmt, myItemType);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof MoneyItem && ((MoneyItem) item).myAmt == myAmt;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    ItemManager im = game.getItemMan();
    if (myAmt == BIG_AMT) return im.bigMoneyIcon;
    if (myAmt == MED_AMT) return im.medMoneyIcon;
    return im.moneyIcon;
  }

  @Override
  public ManiItemType getItemType() {
    return myItemType;
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
}
