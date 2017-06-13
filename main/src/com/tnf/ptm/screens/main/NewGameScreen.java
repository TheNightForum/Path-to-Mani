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

package com.tnf.ptm.screens.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import com.tnf.ptm.screens.controlers.PtmUiScreen;
import com.tnf.ptm.common.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.handler.SaveManager;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.screens.controlers.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements PtmUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl backControl;
    private final PtmUiControl previousControl;
    private final PtmUiControl newControl;

    NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        previousControl = new PtmUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
        previousControl.setDisplayName("Previous Ship");
        controls.add(previousControl);

        newControl = new PtmUiControl(menuLayout.buttonRect(-1, 2), true);
        newControl.setDisplayName("New Ship");
        controls.add(newControl);

        backControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Cancel");
        controls.add(backControl);

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void onAdd(PtmApplication ptmApplication) {
        previousControl.setEnabled(SaveManager.hasPrevShip());
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        MenuScreens screens = ptmApplication.getMenuScreens();
        PtmInputManager im = ptmApplication.getInputMan();
        if (backControl.isJustOff()) {
            im.setScreen(ptmApplication, screens.main);
            return;
        }
        if (previousControl.isJustOff()) {
            ptmApplication.loadNewGame(false, true);
            return;
        }
        if (newControl.isJustOff()) {
            if (!previousControl.isEnabled()) {
                ptmApplication.loadNewGame(false, false);
            } else {
                im.setScreen(ptmApplication, screens.newShip);
            }
        }
    }

    @Override
    public boolean isCursorOnBg(PtmInputManager.InputPointer inputPointer) {
        return true;
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, PtmColor.WHITE);
    }
}
