/*
 * Copyright 2016 BurntGameProductions
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

package com.pathtomani.menu;

import com.pathtomani.ManiApplication;
import com.pathtomani.game.SaveManager;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.ui.ManiUiScreen;
import com.pathtomani.ui.UiDrawer;
import com.pathtomani.GameOptions;
import com.pathtomani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements ManiUiScreen {
  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBackCtrl;
  private final ManiUiControl myPrevCtrl;
  private final ManiUiControl myNewCtrl;

  public NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myPrevCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
    myPrevCtrl.setDisplayName("Previous Ship");
    myControls.add(myPrevCtrl);

    myNewCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true);
    myNewCtrl.setDisplayName("New Ship");
    myControls.add(myNewCtrl);

    myBackCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myBackCtrl.setDisplayName("Cancel");
    myControls.add(myBackCtrl);

  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(ManiApplication cmp) {
    myPrevCtrl.setEnabled(SaveManager.hasPrevShip());
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    MenuScreens screens = cmp.getMenuScreens();
    ManiInputManager im = cmp.getInputMan();
    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.main);
      return;
    }
    if (myPrevCtrl.isJustOff()) {
      cmp.loadNewGame(false, true);
      return;
    }
    if (myNewCtrl.isJustOff()) {
      if (!myPrevCtrl.isEnabled()) {
        cmp.loadNewGame(false, false);
      } else {
        im.setScreen(cmp, screens.newShip);
      }
    }
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return true;
  }

  @Override
  public void blurCustom(ManiApplication cmp) {
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

}
