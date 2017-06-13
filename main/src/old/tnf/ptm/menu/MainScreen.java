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
package old.tnf.ptm.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import old.tnf.ptm.PtmApplication;
import old.tnf.ptm.common.PtmColor;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.ui.PtmUiScreen;
import old.tnf.ptm.ui.UiDrawer;
import old.tnf.ptm.GameOptions;
import old.tnf.ptm.assets.Assets;
import old.tnf.ptm.game.DebugOptions;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements PtmUiScreen {
    private final boolean isMobile;
    private final GameOptions gameOptions;

    private final TextureAtlas.AtlasRegion logoTex;
    public final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl tutorialControl;
    private final PtmUiControl optionsControl;
    private final PtmUiControl exitControl;
    private final PtmUiControl newGameControl;
    private final PtmUiControl creditsControl;

    MainScreen(MenuLayout menuLayout, boolean isMobile, float resolutionRatio, GameOptions gameOptions) {
        this.isMobile = isMobile;
        this.gameOptions = gameOptions;

        tutorialControl = new PtmUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.T);
        tutorialControl.setDisplayName("Tutorial");
        controls.add(tutorialControl);

        newGameControl = new PtmUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyShoot());
        newGameControl.setDisplayName("New Game");
        controls.add(newGameControl);

        optionsControl = new PtmUiControl(isMobile ? null : menuLayout.buttonRect(-1, 3), true, Input.Keys.O);
        optionsControl.setDisplayName("Options");
        controls.add(optionsControl);

        exitControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        exitControl.setDisplayName("Exit");
        controls.add(exitControl);

        creditsControl = new PtmUiControl(MenuLayout.bottomRightFloatingButton(resolutionRatio), true, Input.Keys.C);
        creditsControl.setDisplayName("Credits");
        controls.add(creditsControl);

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
        logoTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuLogo"), Texture.TextureFilter.Linear);
    }

    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (ptmApplication.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
            tutorialControl.setEnabled(false);
        } else {
            tutorialControl.setEnabled(true);
        }

        if (tutorialControl.isJustOff()) {
            ptmApplication.loadNewGame(true, false);
            return;
        }

        PtmInputManager inputManager = ptmApplication.getInputMan();
        MenuScreens screens = ptmApplication.getMenuScreens();

        if (newGameControl.isJustOff()) {
            inputManager.setScreen(ptmApplication, screens.newGame);
            return;
        }

        if (optionsControl.isJustOff()) {
            inputManager.setScreen(ptmApplication, screens.options);
            return;
        }

        if (exitControl.isJustOff()) {
            // Save the settings on exit, but not on mobile as settings don't exist there.
            if (!isMobile) {
                ptmApplication.getOptions().save();
            }
            Gdx.app.exit();
            return;
        }

        if (creditsControl.isJustOff()) {
            inputManager.setScreen(ptmApplication, screens.credits);
        }
    }

    @Override
    public void onAdd(PtmApplication ptmApplication) {
        ptmApplication.getMusicManager().playMenuMusic(gameOptions);
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, PtmColor.WHITE);
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        final float sy = .35f;
        final float sx = sy * 400 / 218;
        if (!DebugOptions.PRINT_BALANCE) {
            uiDrawer.draw(logoTex, sx, sy, sx / 2, sy / 2, uiDrawer.r / 2, 0.1f + sy / 2, 0, PtmColor.WHITE);
        }
    }
}
