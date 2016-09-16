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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.pathtomani.common.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.gfx.TextureManager;
import com.pathtomani.gfx.ManiColor;
import com.pathtomani.game.DebugOptions;
import com.pathtomani.game.sound.MusicManager;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.ui.ManiUiControl;
import com.pathtomani.ui.ManiUiScreen;
import com.pathtomani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen implements ManiUiScreen {
  public static final float CREDITS_BTN_W = .15f;
  public static final float CREDITS_BTN_H = .07f;

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myTutCtrl;
  private final ManiUiControl myNewCtrl;
  private final ManiUiControl myPrevCtrl;
  private final ManiUiControl myBackCtrl;
  private final TextureAtlas.AtlasRegion myTitleTex;
  GameOptions gameOptions;

  public PlayScreen(MenuLayout menuLayout, TextureManager textureManager, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();
    this.gameOptions = gameOptions;

    myTutCtrl = new ManiUiControl(menuLayout.buttonRect(-1,1), true, Input.Keys.ENTER);
    myTutCtrl.setDisplayName("Tutorial");
    myControls.add(myTutCtrl);

    myNewCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.O);
    myNewCtrl.setDisplayName("New Game");
    myControls.add(myNewCtrl);

    myPrevCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.C);
    myPrevCtrl.setDisplayName("Load Game");
    myControls.add(myPrevCtrl);

    myBackCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myBackCtrl.setDisplayName("Back");
    myControls.add(myBackCtrl);



    myTitleTex = textureManager.getTex("ui/title", null);
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
    if (cmp.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
      myTutCtrl.setEnabled(false);
    } else {
      myTutCtrl.setEnabled(true);
    }
    if (myTutCtrl.isJustOff()) {
      cmp.loadNewGame(true, false);
      return;
    }
    if (myNewCtrl.isJustOff()) {
      if (!myPrevCtrl.isEnabled()) {
        cmp.loadNewGame(false, false);
      } else {
        im.setScreen(cmp, screens.newShip);
      }
    }
    if (myPrevCtrl.isJustOff()) {
      cmp.loadNewGame(false, true);
      return;
    }
    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.main);
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
