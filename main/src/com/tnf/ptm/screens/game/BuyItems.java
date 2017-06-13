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

package com.tnf.ptm.screens.game;

import com.tnf.ptm.screens.controlers.PtmUiControl;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.item.ItemContainer;
import old.tnf.ptm.game.item.PtmItem;
import old.tnf.ptm.game.ship.PtmShip;
import com.tnf.ptm.screens.controlers.PtmInputManager;

import java.util.ArrayList;
import java.util.List;

public class BuyItems implements InventoryOperations {
    public final PtmUiControl buyControl;
    private final ArrayList<PtmUiControl> controls = new ArrayList<>();

    BuyItems(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        buyControl = new PtmUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyBuyItem());
        buyControl.setDisplayName("Buy");
        controls.add(buyControl);
    }

    @Override
    public ItemContainer getItems(PtmGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getItems();
    }

    @Override
    public String getHeader() {
        return "Buy:";
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        PtmGame game = ptmApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        PtmShip hero = game.getHero();
        TalkScreen talkScreen = game.getScreens().talkScreen;
        PtmShip target = talkScreen.getTarget();
        if (talkScreen.isTargetFar(hero)) {
            ptmApplication.getInputMan().setScreen(ptmApplication, game.getScreens().mainScreen);
            return;
        }
        PtmItem selItem = is.getSelectedItem();
        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice() && hero.getItemContainer().canAdd(selItem);
        buyControl.setDisplayName(enabled ? "Buy" : "---");
        buyControl.setEnabled(enabled);
        if (!enabled) {
            return;
        }
        if (buyControl.isJustOff()) {
            target.getTradeContainer().getItems().remove(selItem);
            hero.getItemContainer().add(selItem);
            hero.setMoney(hero.getMoney() - selItem.getPrice());
        }
    }
}
