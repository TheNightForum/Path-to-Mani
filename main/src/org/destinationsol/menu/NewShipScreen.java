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

import com.badlogic.gdx.Input;
import org.destinationsol.GameOptions;
import org.destinationsol.ManiApplication;
import org.destinationsol.common.ManiColor;
import org.destinationsol.ui.*;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements ManiUiScreen {
  private final List<ManiUiControl> myControls;
  private final ManiUiControl myOkCtrl;
  public final ManiUiControl myCancelCtrl;

  public NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();
    myOkCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
    myOkCtrl.setDisplayName("OK");
    myControls.add(myOkCtrl);

    myCancelCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myCancelCtrl.setDisplayName("Cancel");
    myControls.add(myCancelCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(ManiApplication cmp) {
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (myCancelCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().newGame);
      return;
    }
    if (myOkCtrl.isJustOff()) {
      cmp.loadNewGame(false, false);
    }
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
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
    uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, ManiColor.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
