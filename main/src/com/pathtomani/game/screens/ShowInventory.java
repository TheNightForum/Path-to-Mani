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

import com.pathtomani.common.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.item.ItemContainer;
import com.pathtomani.game.item.ManiItem;
import com.pathtomani.game.ship.ManiShip;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.ui.UiDrawer;
import com.pathtomani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class ShowInventory implements InventoryOperations {

  private final List<ManiUiControl> myControls;
  public final ManiUiControl eq1Ctrl;
  public final ManiUiControl eq2Ctrl;
  public final ManiUiControl dropCtrl;

  public ShowInventory(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    eq1Ctrl = new ManiUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyEquip());
    eq1Ctrl.setDisplayName("Eq");
    myControls.add(eq1Ctrl);

    eq2Ctrl = new ManiUiControl(inventoryScreen.itemCtrl(1), true, gameOptions.getKeyEquip2());
    eq2Ctrl.setDisplayName("Eq2");
    myControls.add(eq2Ctrl);

    dropCtrl = new ManiUiControl(inventoryScreen.itemCtrl(2), true, gameOptions.getKeyDrop());
    dropCtrl.setDisplayName("Drop");
    myControls.add(dropCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame g = cmp.getGame();
    InventoryScreen is = g.getScreens().inventoryScreen;
    ManiItem selItem = is.getSelectedItem();
    ManiShip hero = g.getHero();

    eq1Ctrl.setDisplayName("---");
    eq1Ctrl.setEnabled(false);
    eq2Ctrl.setDisplayName("---");
    eq2Ctrl.setEnabled(false);
    dropCtrl.setEnabled(false);

    if (selItem == null || hero == null) {
      return;
    }


    dropCtrl.setEnabled(true);
    if (dropCtrl.isJustOff()) {
      ItemContainer ic = hero.getItemContainer();
      is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
      hero.dropItem(cmp.getGame(), selItem);
      return;
    }

    Boolean equipped1 = hero.maybeUnequip(g, selItem, false, false);
    boolean canEquip1 = hero.maybeEquip(g, selItem, false, false);
    Boolean equipped2 = hero.maybeUnequip(g, selItem, true, false);
    boolean canEquip2 = hero.maybeEquip(g, selItem, true, false);

    if (equipped1 || canEquip1) {
      eq1Ctrl.setDisplayName(equipped1 ? "Unequip" : "Equip");
      eq1Ctrl.setEnabled(true);
    }
    if (equipped2 || canEquip2) {
      eq2Ctrl.setDisplayName(equipped2 ? "Unequip" : "Set Gun 2");
      eq2Ctrl.setEnabled(true);
    }
    if (eq1Ctrl.isJustOff()) {
      if (equipped1) hero.maybeUnequip(g, selItem, false, true);
      else hero.maybeEquip(g, selItem, false, true);
    }
    if (eq2Ctrl.isJustOff()) {
      if (equipped2) hero.maybeUnequip(g, selItem, true, true);
      else hero.maybeEquip(g, selItem, true, true);
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
    return -1;
  }

  @Override
  public String getHeader() {
    return "Items:";
  }
}
