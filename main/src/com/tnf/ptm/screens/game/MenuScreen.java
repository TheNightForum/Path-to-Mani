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

import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.screens.controlers.PtmUiScreen;
import com.tnf.ptm.common.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.screens.main.MenuLayout;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import com.tnf.ptm.screens.controlers.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements PtmUiScreen {
    private final List<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl closeControl;
    private final PtmUiControl exitControl;
    private final PtmUiControl respawnControl;
    private final PtmUiControl soundVolControl;
    private final PtmUiControl musicVolumeControl;
    private final PtmUiControl doNotSellEquippedControl;

    MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        doNotSellEquippedControl = new PtmUiControl(menuLayout.buttonRect(-1, -1), true);
        doNotSellEquippedControl.setDisplayName("Can sell used items");
        controls.add(doNotSellEquippedControl);
        soundVolControl = new PtmUiControl(menuLayout.buttonRect(-1, 1), true);
        soundVolControl.setDisplayName("Sound Volume");
        controls.add(soundVolControl);
        musicVolumeControl = new PtmUiControl(menuLayout.buttonRect(-1, 0), true);
        musicVolumeControl.setDisplayName("Music Volume");
        controls.add(musicVolumeControl);
        respawnControl = new PtmUiControl(menuLayout.buttonRect(-1, 2), true);
        respawnControl.setDisplayName("Respawn");
        controls.add(respawnControl);
        exitControl = new PtmUiControl(menuLayout.buttonRect(-1, 3), true);
        exitControl.setDisplayName("Exit");
        controls.add(exitControl);
        closeControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
        closeControl.setDisplayName("Resume");
        controls.add(closeControl);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        PtmGame game = ptmApplication.getGame();
        game.setPaused(true);
        PtmInputManager im = ptmApplication.getInputMan();
        GameOptions options = ptmApplication.getOptions();
        soundVolControl.setDisplayName("Sound Volume: " + options.getSFXVolumeAsText());
        if (soundVolControl.isJustOff()) {
            options.advanceSoundVolMul();
        }
        musicVolumeControl.setDisplayName("Music Volume: " + options.getMusicVolumeAsText());
        if (musicVolumeControl.isJustOff()) {
            options.advanceMusicVolMul();
            ptmApplication.getMusicManager().resetVolume(options);
        }
        if (respawnControl.isJustOff()) {
            game.respawn();
            im.setScreen(ptmApplication, game.getScreens().mainScreen);
            game.setPaused(false);
        }
        if (exitControl.isJustOff()) {
            ptmApplication.finishGame();
        }
        if (closeControl.isJustOff()) {
            game.setPaused(false);
            im.setScreen(ptmApplication, game.getScreens().mainScreen);
        }
        doNotSellEquippedControl.setDisplayName("Can sell used items: " +
                                                  (options.canSellEquippedItems ? "Yes" : "No"));
        if (doNotSellEquippedControl.isJustOff()) {
            options.canSellEquippedItems = !options.canSellEquippedItems;
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(uiDrawer.filler, PtmColor.UI_BG);
    }

    @Override
    public boolean isCursorOnBg(PtmInputManager.InputPointer inputPointer) {
        return true;
    }
}
