/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.menu;

import org.destinationsol.GameOptions;
import org.destinationsol.ManiApplication;
import org.destinationsol.game.SaveManager;
import org.destinationsol.ui.ManiInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.ManiUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class NewGameScreen implements ManiUiScreen {
  private final ArrayList<SolUiControl> myControls;
  private final SolUiControl myBackCtrl;
  private final SolUiControl myPrevCtrl;
  private final SolUiControl myNewCtrl;

  public NewGameScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    myPrevCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyShoot());
    myPrevCtrl.setDisplayName("Previous Ship");
    myControls.add(myPrevCtrl);

    myNewCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true);
    myNewCtrl.setDisplayName("New Ship");
    myControls.add(myNewCtrl);

    myBackCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myBackCtrl.setDisplayName("Cancel");
    myControls.add(myBackCtrl);

  }

  @Override
  public List<SolUiControl> getControls() {
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
