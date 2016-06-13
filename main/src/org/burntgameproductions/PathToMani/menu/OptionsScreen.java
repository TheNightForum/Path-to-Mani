/*
 * Copyright 2016 MovingBlocks
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

package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Input;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreen implements ManiUiScreen {
  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBackCtrl;
  private final ManiUiControl myResoCtrl;
  private final ManiUiControl myControlTypeCtrl;
  private final ManiUiControl inputMapCtrl;

  public OptionsScreen(MenuLayout menuLayout, GameOptions gameOptions) {

    myControls = new ArrayList<ManiUiControl>();

    myResoCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myControlTypeCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
    myControlTypeCtrl.setDisplayName("Control Type");
    myControls.add(myControlTypeCtrl);

    inputMapCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.M);
    inputMapCtrl.setDisplayName("Controls");
    myControls.add(inputMapCtrl);

    myBackCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myBackCtrl.setDisplayName("Back");
    myControls.add(myBackCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    if (myResoCtrl.isJustOff()) {
      im.setScreen(cmp, screens.resolutionScreen);
    }

    int ct = cmp.getOptions().controlType;
    String ctName = "Keyboard";
    if (ct == GameOptions.CONTROL_MIXED) ctName = "KB + Mouse";
    if (ct == GameOptions.CONTROL_MOUSE) ctName = "Mouse";
    if (ct == GameOptions.CONTROL_CONTROLLER) ctName = "Controller";
    myControlTypeCtrl.setDisplayName("Input: " + ctName);
    if (myControlTypeCtrl.isJustOff()) {
      cmp.getOptions().advanceControlType(false);
    }
    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.main);
    }


    if (inputMapCtrl.isJustOff()) {
      if (ct == GameOptions.CONTROL_MIXED) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
      } else if (ct == GameOptions.CONTROL_KB) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
      } else if (ct == GameOptions.CONTROL_CONTROLLER) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
      }
      im.setScreen(cmp, screens.inputMapScreen);
    }
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

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
