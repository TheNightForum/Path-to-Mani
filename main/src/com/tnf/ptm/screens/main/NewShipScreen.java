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

import com.badlogic.gdx.Input;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import com.tnf.ptm.screens.controlers.PtmUiScreen;
import com.tnf.ptm.common.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.screens.controlers.FontSize;
import com.tnf.ptm.screens.controlers.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements PtmUiScreen {
    private final List<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl cancelControl;
    private final PtmUiControl okControl;

    NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        okControl = new PtmUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
        okControl.setDisplayName("OK");
        controls.add(okControl);

        cancelControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (cancelControl.isJustOff()) {
            ptmApplication.getInputMan().setScreen(ptmApplication, ptmApplication.getMenuScreens().newGame);
            return;
        }
        if (okControl.isJustOff()) {
            ptmApplication.loadNewGame(false, false);
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, PtmColor.WHITE);
    }
}
