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

package com.tnf.ptm.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.ui.SolUiControl;
import com.tnf.ptm.ui.SolUiScreen;
import com.tnf.ptm.GameOptions;
import com.tnf.ptm.SolApplication;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.common.SolColor;
import com.tnf.ptm.game.SaveManager;
import com.tnf.ptm.ui.SolInputManager;
import com.tnf.ptm.ui.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements SolUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl backControl;
    private final SolUiControl previousControl;
    private final SolUiControl newControl;

    NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        previousControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
        previousControl.setDisplayName("Previous Ship");
        controls.add(previousControl);

        newControl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
        newControl.setDisplayName("New Ship");
        controls.add(newControl);

        backControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Cancel");
        controls.add(backControl);

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        previousControl.setEnabled(SaveManager.hasPrevShip());
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        MenuScreens screens = solApplication.getMenuScreens();
        SolInputManager im = solApplication.getInputMan();
        if (backControl.isJustOff()) {
            im.setScreen(solApplication, screens.main);
            return;
        }
        if (previousControl.isJustOff()) {
            solApplication.loadNewGame(false, true);
            return;
        }
        if (newControl.isJustOff()) {
            if (!previousControl.isEnabled()) {
                solApplication.loadNewGame(false, false);
            } else {
                im.setScreen(solApplication, screens.newShip);
            }
        }
    }

    @Override
    public boolean isCursorOnBg(SolInputManager.InputPointer inputPointer) {
        return true;
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, SolColor.WHITE);
    }
}
