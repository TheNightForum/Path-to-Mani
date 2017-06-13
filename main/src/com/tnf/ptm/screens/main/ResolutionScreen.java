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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import com.tnf.ptm.screens.controlers.PtmUiScreen;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.screens.controlers.FontSize;
import com.tnf.ptm.screens.controlers.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class ResolutionScreen implements PtmUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<PtmUiControl> myControls = new ArrayList<>();
    private final PtmUiControl closeControl;
    private final PtmUiControl resolutionControl;
    private final PtmUiControl fullscreenControl;

    ResolutionScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        resolutionControl = new PtmUiControl(menuLayout.buttonRect(-1, 2), true);
        resolutionControl.setDisplayName("Resolution");
        myControls.add(resolutionControl);

        fullscreenControl = new PtmUiControl(menuLayout.buttonRect(-1, 3), true);
        fullscreenControl.setDisplayName("Fullscreen");
        myControls.add(fullscreenControl);

        closeControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        closeControl.setDisplayName("Back");
        myControls.add(closeControl);

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return myControls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        PtmInputManager inputManager = ptmApplication.getInputMan();
        GameOptions options = ptmApplication.getOptions();

        if (closeControl.isJustOff()) {
            Gdx.graphics.setDisplayMode(options.x, options.y, options.fullscreen);
            inputManager.setScreen(ptmApplication, ptmApplication.getMenuScreens().options);
            return;
        }

        resolutionControl.setDisplayName(options.x + "x" + options.y);
        if (resolutionControl.isJustOff()) {
            options.advanceReso();
        }

        fullscreenControl.setDisplayName(options.fullscreen ? "Fullscreen" : "Windowed");
        if (fullscreenControl.isJustOff()) {
            options.advanceFullscreen();
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, PtmColor.WHITE);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.drawString("Click 'Back' to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, PtmColor.WHITE);
    }
}
