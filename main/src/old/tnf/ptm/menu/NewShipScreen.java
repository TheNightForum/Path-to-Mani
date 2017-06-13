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

import com.badlogic.gdx.Input;
import com.tnf.ptm.common.SolColor;
import com.tnf.ptm.ui.SolInputManager;
import com.tnf.ptm.ui.SolUiControl;
import com.tnf.ptm.ui.SolUiScreen;
import com.tnf.ptm.GameOptions;
import com.tnf.ptm.SolApplication;
import com.tnf.ptm.ui.FontSize;
import com.tnf.ptm.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements SolUiScreen {
    private final List<SolUiControl> controls = new ArrayList<>();
    private final SolUiControl cancelControl;
    private final SolUiControl okControl;

    NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        okControl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
        okControl.setDisplayName("OK");
        controls.add(okControl);

        cancelControl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);
    }

    @Override
    public List<SolUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (cancelControl.isJustOff()) {
            solApplication.getInputMan().setScreen(solApplication, solApplication.getMenuScreens().newGame);
            return;
        }
        if (okControl.isJustOff()) {
            solApplication.loadNewGame(false, false);
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.WHITE);
    }
}
