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

import com.pathtomani.ManiApplication;
import com.pathtomani.game.ManiGame;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.ui.UiDrawer;
import com.pathtomani.GameOptions;
import com.pathtomani.game.item.ItemContainer;
import com.pathtomani.game.item.ManiItem;
import com.pathtomani.game.ship.ManiShip;
import com.pathtomani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class BuyItems implements InventoryOperations {

  private final ArrayList<ManiUiControl> myControls;
  public final ManiUiControl buyCtrl;

  public BuyItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    buyCtrl = new ManiUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyBuyItem());
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);
  }

  @Override
  public ItemContainer getItems(ManiGame game) {
    return game.getScreens().talkScreen.getTarget().getTradeContainer().getItems();
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
    return "Buy:";
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
    ManiShip target = talkScreen.getTarget();
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    ManiItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice() && hero.getItemContainer().canAdd(selItem);
    buyCtrl.setDisplayName(enabled ? "Buy" : "---");
    buyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (buyCtrl.isJustOff()) {
      target.getTradeContainer().getItems().remove(selItem);
      hero.getItemContainer().add(selItem);
      hero.setMoney(hero.getMoney() - selItem.getPrice());
    }
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

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
  public void blurCustom(ManiApplication cmp) {

  }
}
