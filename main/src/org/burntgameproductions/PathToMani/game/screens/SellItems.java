

package org.burntgameproductions.PathToMani.game.screens;

import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.item.ItemContainer;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class SellItems implements InventoryOperations {

  public static float PERC = .8f;
  private final ArrayList<ManiUiControl> myControls;
  public final ManiUiControl sellCtrl;

  public SellItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    sellCtrl = new ManiUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeySellItem());
    sellCtrl.setDisplayName("Sell");
    myControls.add(sellCtrl);
  }

  @Override
  public ItemContainer getItems(ManiGame game) {
    ManiShip h = game.getHero();
    return h == null ? null : h.getItemContainer();
  }

  @Override
  public boolean isUsing(ManiGame game, ManiItem item) {
    ManiShip h = game.getHero();
    return h != null && h.maybeUnequip(game, item, false);
  }

  @Override
  public float getPriceMul() {
    return PERC;
  }

  @Override
  public String getHeader() {
    return "Sell:";
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    TalkScreen talkScreen = game.getScreens().talkScreen;
    ManiShip target = talkScreen.getTarget();
    ManiShip hero = game.getHero();
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    ManiItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && target.getTradeContainer().getItems().canAdd(selItem);
    sellCtrl.setDisplayName(enabled ? "Sell" : "---");
    sellCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (sellCtrl.isJustOff()) {
      ItemContainer ic = hero.getItemContainer();
      is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
      ic.remove(selItem);
      target.getTradeContainer().getItems().add(selItem);
      hero.setMoney(hero.getMoney() + selItem.getPrice() * PERC);
    }
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
