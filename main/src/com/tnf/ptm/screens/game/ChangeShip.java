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

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.item.Engine;
import old.tnf.ptm.game.item.Gun;
import old.tnf.ptm.game.item.ItemContainer;
import old.tnf.ptm.game.item.PtmItem;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.game.ship.hulls.Hull;
import old.tnf.ptm.game.ship.hulls.HullConfig;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import com.tnf.ptm.common.GameOptions;
import old.tnf.ptm.game.item.ShipItem;
import old.tnf.ptm.game.ship.ShipRepairer;

import java.util.ArrayList;
import java.util.List;

public class ChangeShip implements InventoryOperations {
    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl changeControl;

    ChangeShip(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        changeControl = new PtmUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyChangeShip());
        changeControl.setDisplayName("Change");
        controls.add(changeControl);
    }

    @Override
    public ItemContainer getItems(PtmGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getShips();
    }

    @Override
    public String getHeader() {
        return "Ships:";
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
        if (talkScreen.isTargetFar(hero)) {
            ptmApplication.getInputMan().setScreen(ptmApplication, game.getScreens().mainScreen);
            return;
        }
        PtmItem selItem = is.getSelectedItem();
        if (selItem == null) {
            changeControl.setDisplayName("---");
            changeControl.setEnabled(false);
            return;
        }
        boolean enabled = hasMoneyToBuyShip(hero, selItem);
        boolean sameShip = isSameShip(hero, selItem);
        if (enabled && !sameShip) {
            changeControl.setDisplayName("Change");
            changeControl.setEnabled(true);
        } else if (enabled && sameShip) {
            changeControl.setDisplayName("Have it");
            changeControl.setEnabled(false);
            return;
        } else {
            changeControl.setDisplayName("---");
            changeControl.setEnabled(false);
            return;
        }
        if (changeControl.isJustOff()) {
            hero.setMoney(hero.getMoney() - selItem.getPrice());
            changeShip(game, hero, (ShipItem) selItem);
        }
    }

    private boolean hasMoneyToBuyShip(PtmShip hero, PtmItem shipToBuy) {
        return hero.getMoney() >= shipToBuy.getPrice();
    }

    private boolean isSameShip(PtmShip hero, PtmItem shipToBuy) {
        if (shipToBuy instanceof ShipItem) {
            ShipItem ship = (ShipItem) shipToBuy;
            HullConfig config1 = ship.getConfig();
            HullConfig config2 = hero.getHull().getHullConfig();
            return config1.equals(config2);
        } else {
            throw new IllegalArgumentException("ChangeShip:isSameShip received " + shipToBuy.getClass() + " argument instead of ShipItem!");
        }
    }

    private void changeShip(PtmGame game, PtmShip hero, ShipItem selected) {
        HullConfig newConfig = selected.getConfig();
        Hull hull = hero.getHull();
        Engine.Config ec = newConfig.getEngineConfig();
        Engine ei = ec == null ? null : ec.example.copy();
        Gun g2 = hull.getGun(true);
        PtmShip newHero = game.getShipBuilder().build(game, hero.getPosition(), new Vector2(), hero.getAngle(), 0, hero.getPilot(),
                hero.getItemContainer(), newConfig, newConfig.getMaxLife(), hull.getGun(false), g2, null,
                ei, new ShipRepairer(), hero.getMoney(), hero.getTradeContainer(), hero.getShield(), hero.getArmor());
        game.getObjMan().removeObjDelayed(hero);
        game.getObjMan().addObjDelayed(newHero);
    }
}
