/*
 * Copyright 2016 BurntGameProductions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pathtomani.game.screens;

import com.pathtomani.game.ManiGame;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.ui.UiDrawer;
import com.pathtomani.common.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.game.item.ItemContainer;
import com.pathtomani.game.item.ManiItem;
import com.pathtomani.game.ship.ManiShip;
import com.pathtomani.ui.ManiUiControl;

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
