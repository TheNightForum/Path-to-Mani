/*
 * Copyright 2017 TheNightForum
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
package old.tnf.ptm.game.screens;

import old.tnf.ptm.PtmApplication;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.item.ItemContainer;
import old.tnf.ptm.game.item.PtmItem;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.GameOptions;

import java.util.ArrayList;
import java.util.List;

public class SellItems implements InventoryOperations {
    private static float PERC = .8f;

    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl sellControl;

    SellItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        sellControl = new PtmUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeySellItem());
        sellControl.setDisplayName("Sell");
        controls.add(sellControl);
    }

    @Override
    public ItemContainer getItems(PtmGame game) {
        PtmShip h = game.getHero();
        return h == null ? null : h.getItemContainer();
    }

    @Override
    public boolean isUsing(PtmGame game, PtmItem item) {
        PtmShip h = game.getHero();
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
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        PtmGame game = ptmApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        TalkScreen talkScreen = game.getScreens().talkScreen;
        PtmShip target = talkScreen.getTarget();
        PtmShip hero = game.getHero();
        if (talkScreen.isTargetFar(hero)) {
            ptmApplication.getInputMan().setScreen(ptmApplication, game.getScreens().mainScreen);
            return;
        }
        PtmItem selItem = is.getSelectedItem();
        if (selItem == null) {
            sellControl.setDisplayName("----");
            sellControl.setEnabled(false);
            return;
        }

        boolean isWornAndCanBeSold = isItemEquippedAndSellable(selItem, ptmApplication.getOptions());
        boolean enabled = isItemSellable(selItem, target);

        if (enabled && isWornAndCanBeSold) {
            sellControl.setDisplayName("Sell");
            sellControl.setEnabled(true);
        } else if (enabled && !isWornAndCanBeSold) {
            sellControl.setDisplayName("Unequip it!");
            sellControl.setEnabled(false);
        } else {
            sellControl.setDisplayName("----");
            sellControl.setEnabled(false);
        }

        if (!enabled || !isWornAndCanBeSold) {
            return;
        }
        if (sellControl.isJustOff()) {
            ItemContainer ic = hero.getItemContainer();
            is.setSelected(ic.getSelectionAfterRemove(is.getSelected()));
            ic.remove(selItem);
            target.getTradeContainer().getItems().add(selItem);
            hero.setMoney(hero.getMoney() + selItem.getPrice() * PERC);
        }
    }

    private boolean isItemSellable(PtmItem item, PtmShip target) {
        return target.getTradeContainer().getItems().canAdd(item);
    }

    // Return true if the item is not worn, or is worn and canSellEquippedItems is true
    private boolean isItemEquippedAndSellable(PtmItem item, GameOptions options) {
        return (item.isEquipped() == 0 || options.canSellEquippedItems);
    }
}
