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

import com.tnf.ptm.common.PtmColor;
import old.tnf.ptm.ui.PtmUiScreen;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.menu.MenuLayout;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.ui.UiDrawer;

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
