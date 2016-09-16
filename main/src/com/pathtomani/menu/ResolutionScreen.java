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

import com.badlogic.gdx.Gdx;
import com.pathtomani.common.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.ui.*;
import com.pathtomani.gfx.ManiColor;

import java.util.ArrayList;
import java.util.List;

public class ResolutionScreen implements ManiUiScreen {

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myCloseCtrl;
  private final ManiUiControl myResoCtrl;
  private final ManiUiControl myFsCtrl;

  public ResolutionScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myResoCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myFsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true);
    myFsCtrl.setDisplayName("Fullscreen");
    myControls.add(myFsCtrl);

    myCloseCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myCloseCtrl.setDisplayName("Back");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiInputManager im = cmp.getInputMan();
    if (myCloseCtrl.isJustOff()) {
      GameOptions options = cmp.getOptions();
      Gdx.graphics.setDisplayMode(options.x, options.y, options.fullscreen);
      im.setScreen(cmp, cmp.getMenuScreens().options);
      return;
    }

    GameOptions options = cmp.getOptions();
    myResoCtrl.setDisplayName(options.x + "x" + options.y);
    if (myResoCtrl.isJustOff()) {
      options.advanceReso();
    }
    myFsCtrl.setDisplayName(options.fullscreen ? "Fullscreen" : "Windowed");
    if (myFsCtrl.isJustOff()) {
      options.advanceFullscreen();
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
    uiDrawer.drawString("Click 'Back' to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, ManiColor.W);
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
