

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.game.item.EngineItem;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.gun.GunItem;
import org.burntgameproductions.PathToMani.game.item.ItemContainer;
import org.burntgameproductions.PathToMani.game.item.ShipItem;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.ship.ShipRepairer;
import org.burntgameproductions.PathToMani.game.ship.hulls.Hull;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class ChangeShip implements InventoryOperations {

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBuyCtrl;

  public ChangeShip(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myBuyCtrl = new ManiUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyChangeShip());
    myBuyCtrl.setDisplayName("Change");
    myControls.add(myBuyCtrl);
  }

  @Override
  public ItemContainer getItems(ManiGame game) {
    return game.getScreens().talkScreen.getTarget().getTradeContainer().getShips();
  }

  @Override
  public boolean isUsing(ManiGame game, ManiItem item) {
    return false;
  }

  @Override
  public float getPriceMul() {
    return 1;
  }

  @Override
  public String getHeader() {
    return "Ships:";
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    ManiShip hero = game.getHero();
    TalkScreen talkScreen = game.getScreens().talkScreen;
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    ManiItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
    myBuyCtrl.setDisplayName(enabled ? "Change" : "---");
    myBuyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (myBuyCtrl.isJustOff()) {
      hero.setMoney(hero.getMoney() - selItem.getPrice());
      changeShip(game, hero, (ShipItem) selItem);
    }
  }

  private void changeShip(ManiGame game, ManiShip hero, ShipItem selected) {
    HullConfig newConfig = selected.getConfig();
    Hull hull = hero.getHull();
    EngineItem.Config ec = newConfig.getEngineConfig();
    EngineItem ei = ec == null ? null : ec.example.copy();
    GunItem g2 = hull.getGun(true);
    ManiShip newHero = game.getShipBuilder().build(game, hero.getPosition(), new Vector2(), hero.getAngle(), 0, hero.getPilot(),
      hero.getItemContainer(), newConfig, newConfig.getMaxLife(), hull.getGun(false), g2, null,
      ei, new ShipRepairer(), hero.getMoney(), hero.getTradeContainer(), hero.getShield(), hero.getArmor());
    game.getObjMan().removeObjDelayed(hero);
    game.getObjMan().addObjDelayed(newHero);
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
}
