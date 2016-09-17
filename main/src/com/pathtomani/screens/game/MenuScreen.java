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

package com.pathtomani.screens.game;

import com.pathtomani.common.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.gfx.ManiColor;
import com.pathtomani.game.ManiGame;
import com.pathtomani.screens.menu.MenuLayout;
import com.pathtomani.screens.controllers.ManiInputManager;
import com.pathtomani.screens.controllers.ManiUiControl;
import com.pathtomani.screens.controllers.ManiUiScreen;
import com.pathtomani.screens.controllers.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements ManiUiScreen {
  private final List<ManiUiControl> myControls;
  private final ManiUiControl myCloseCtrl;
  private final ManiUiControl myExitCtrl;
  private final ManiUiControl myRespawnCtrl;
  private final ManiUiControl mySoundVolCtrl;
  private final ManiUiControl myMusVolCtrl;

  public MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    mySoundVolCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true);
    mySoundVolCtrl.setDisplayName("Sound Vol");
    myControls.add(mySoundVolCtrl);
    myMusVolCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 0), true);
    myMusVolCtrl.setDisplayName("Music Vol");
    myControls.add(myMusVolCtrl);
    myRespawnCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true);
    myRespawnCtrl.setDisplayName("Respawn");
    myControls.add(myRespawnCtrl);
    myExitCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);
    myCloseCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    myCloseCtrl.setDisplayName("Resume");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame g = cmp.getGame();
    g.setPaused(true);
    ManiInputManager im = cmp.getInputMan();
    GameOptions options = cmp.getOptions();
    mySoundVolCtrl.setDisplayName("Sound Volume: " + getVolName(options));
    if (mySoundVolCtrl.isJustOff()) {
      options.advanceSoundVolMul();
    }
    myMusVolCtrl.setDisplayName("Music Volume: " + getMusName(options));
    if(myMusVolCtrl.isJustOff()){
    	options.advanceMusicVolMul();
    }
    if (myRespawnCtrl.isJustOff()) {
      g.respawn();
      im.setScreen(cmp, g.getScreens().mainScreen);
      g.setPaused(false);
    }
    if (myExitCtrl.isJustOff()) {
      cmp.finishGame();
    }
    if (myCloseCtrl.isJustOff()) {
      g.setPaused(false);
      im.setScreen(cmp, g.getScreens().mainScreen);
    }
  }

  private String getVolName(GameOptions options) {
    float volMul = options.volMul;
    if (volMul == 0) return "Off";
    else if (volMul < .4f) return "Low";
    else if (volMul < .7f) return "High";
    else {return "Max";}
  }
  private String getMusName(GameOptions options)
  {
	  float musMul = options.musicMul;
	  if (musMul == 0) return "Off";
	  if (musMul < .4f) return "Low";
	  if (musMul < .7f) return "High";
	  return "Max";
  }
  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
    uiDrawer.draw(uiDrawer.filler, ManiColor.UI_BG);
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
    return true;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
