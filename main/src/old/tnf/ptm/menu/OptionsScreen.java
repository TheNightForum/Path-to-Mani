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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.common.PtmColor;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.ui.PtmUiScreen;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.assets.Assets;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreen implements PtmUiScreen {
    private final TextureAtlas.AtlasRegion bgTex;

    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl backControl;
    private final PtmUiControl resolutionControl;
    private final PtmUiControl inputTypeControl;
    private final PtmUiControl inputMapControl;
    private final PtmUiControl soundVolumeControl;
    private final PtmUiControl musicVolumeControl;

    OptionsScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        resolutionControl = new PtmUiControl(menuLayout.buttonRect(-1, 1), true);
        resolutionControl.setDisplayName("Resolution");
        controls.add(resolutionControl);

        inputTypeControl = new PtmUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
        inputTypeControl.setDisplayName("Control Type");
        controls.add(inputTypeControl);

        inputMapControl = new PtmUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.M);
        inputMapControl.setDisplayName("Controls");
        controls.add(inputMapControl);

        backControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        backControl.setDisplayName("Back");
        controls.add(backControl);

        soundVolumeControl = new PtmUiControl(menuLayout.buttonRect(-1, 0), true);
        soundVolumeControl.setDisplayName("Sound Volume");
        controls.add(soundVolumeControl);

        musicVolumeControl = new PtmUiControl(menuLayout.buttonRect(-1, -1), true);
        musicVolumeControl.setDisplayName("Music Volume");
        controls.add(musicVolumeControl);

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        PtmInputManager inputManager = ptmApplication.getInputMan();
        MenuScreens screens = ptmApplication.getMenuScreens();
        GameOptions options = ptmApplication.getOptions();
        if (resolutionControl.isJustOff()) {
            inputManager.setScreen(ptmApplication, screens.resolutionScreen);
        }

        int controlType = ptmApplication.getOptions().controlType;
        String controlName = "Keyboard";
        if (controlType == GameOptions.CONTROL_MIXED) {
            controlName = "KB + Mouse";
        }
        if (controlType == GameOptions.CONTROL_MOUSE) {
            controlName = "Mouse";
        }
        if (controlType == GameOptions.CONTROL_CONTROLLER) {
            controlName = "Controller";
        }
        inputTypeControl.setDisplayName("Input: " + controlName);
        if (inputTypeControl.isJustOff()) {
            ptmApplication.getOptions().advanceControlType(false);
        }

        if (backControl.isJustOff()) {
            inputManager.setScreen(ptmApplication, screens.main);
        }

        if (inputMapControl.isJustOff()) {
            if (controlType == GameOptions.CONTROL_MIXED) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
            } else if (controlType == GameOptions.CONTROL_KB) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
            } else if (controlType == GameOptions.CONTROL_CONTROLLER) {
                screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
            }
            inputManager.setScreen(ptmApplication, screens.inputMapScreen);
        }

        soundVolumeControl.setDisplayName("Sound Volume: " + options.getSFXVolumeAsText());
        if (soundVolumeControl.isJustOff()) {
            options.advanceSoundVolMul();
        }

        musicVolumeControl.setDisplayName("Music Volume: " + options.getMusicVolumeAsText());
        if (musicVolumeControl.isJustOff()) {
            options.advanceMusicVolMul();
            ptmApplication.getMusicManager().resetVolume(options);
        }
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, PtmColor.WHITE);
    }
}
