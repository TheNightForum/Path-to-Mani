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

package com.pathtomani.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.pathtomani.ManiApplication;
import com.pathtomani.managers.sound.MusicManager;
import com.pathtomani.screens.controllers.ManiInputManager;
import com.pathtomani.screens.controllers.ManiUiScreen;
import com.pathtomani.screens.controllers.UiDrawer;
import com.pathtomani.common.GameOptions;
import com.pathtomani.gfx.TextureManager;
import com.pathtomani.gfx.ManiColor;
import com.pathtomani.game.DebugOptions;
import com.pathtomani.screens.controllers.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements ManiUiScreen {
  public static final float CREDITS_BTN_W = .15f;
  public static final float CREDITS_BTN_H = .07f;

  private final ArrayList<ManiUiControl> myControls;
  //private final ManiUiControl myTutCtrl;
  //private final ManiUiControl myOptionsCtrl;
  //private final ManiUiControl myExitCtrl;
  //private final ManiUiControl myNewGameCtrl;
  //private final ManiUiControl myCreditsCtrl;
  private final ManiUiControl myPlayCtrl;
  private final ManiUiControl myOptionsCtrl;
  private final ManiUiControl myCreditsCtrl;
  private final ManiUiControl myExitCtrl;
  private final TextureAtlas.AtlasRegion myTitleTex;
  private final boolean isMobile;
  GameOptions gameOptions;

  public MainScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    isMobile = mobile;
    myControls = new ArrayList<ManiUiControl>();
    this.gameOptions = gameOptions;

    myPlayCtrl = new ManiUiControl(menuLayout.buttonRect(-1,1), true, Input.Keys.ENTER);
    myPlayCtrl.setDisplayName("Play");
    myControls.add(myPlayCtrl);

    myOptionsCtrl = new ManiUiControl(mobile ? null : menuLayout.buttonRect(-1, 2), true, Input.Keys.O);
    myOptionsCtrl.setDisplayName("Options");
    myControls.add(myOptionsCtrl);

    myCreditsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.C);
    myCreditsCtrl.setDisplayName("Credits");
    myControls.add(myCreditsCtrl);

    myExitCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);



    myTitleTex = textureManager.getTex("controllers/title", null);
  }

  public static Rectangle creditsBtnRect(float r) {
    return new Rectangle(r - CREDITS_BTN_W, 1 - CREDITS_BTN_H, CREDITS_BTN_W, CREDITS_BTN_H);
  }

  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    if (myPlayCtrl.isJustOff()) {
      im.setScreen(cmp, screens.playScreen);
      return;
    }
    if (myOptionsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.options);
      return;
    }
    if (myCreditsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.credits);
    }
    if (myExitCtrl.isJustOff()) {
      // Save the settings on exit, but not on mobile as settings don't exist there.
      if (isMobile == false) {
        cmp.getOptions().save();
      }
      Gdx.app.exit();
      return;
    }
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {
    MusicManager.getInstance().PlayMenuMusic(gameOptions);
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
    float sz = .55f;
    if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(myTitleTex, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
