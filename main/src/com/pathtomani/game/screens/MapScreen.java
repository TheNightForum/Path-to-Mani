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

package com.pathtomani.game.screens;

import com.badlogic.gdx.math.Rectangle;
import com.pathtomani.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.MapDrawer;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.ui.ManiUiScreen;
import com.pathtomani.ui.UiDrawer;
import com.pathtomani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class MapScreen implements ManiUiScreen {
  private final List<ManiUiControl> myControls;
  public final ManiUiControl closeCtrl;
  public final ManiUiControl zoomInCtrl;
  public final ManiUiControl zoomOutCtrl;

  public MapScreen(RightPaneLayout rightPaneLayout, boolean mobile, float r, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    Rectangle closeArea = mobile ? MainScreen.btn(0, MainScreen.HELPER_ROW_1, true) : rightPaneLayout.buttonRect(1);
    closeCtrl = new ManiUiControl(closeArea, true, gameOptions.getKeyMap(), gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);
    float row0 = 1 - MainScreen.CELL_SZ;
    float row1 = row0 - MainScreen.CELL_SZ;
    float colN = r - MainScreen.CELL_SZ;
    Rectangle zoomInArea = mobile ? MainScreen.btn(0, row1, false) : rightPaneLayout.buttonRect(2);
    zoomInCtrl = new ManiUiControl(zoomInArea, true, gameOptions.getKeyZoomIn());
    zoomInCtrl.setDisplayName("Zoom In");
    myControls.add(zoomInCtrl);
    Rectangle zoomOutArea = mobile ? MainScreen.btn(0, row0, false) : rightPaneLayout.buttonRect(3);
    zoomOutCtrl = new ManiUiControl(zoomOutArea, true, gameOptions.getKeyZoomOut());
    zoomOutCtrl.setDisplayName("Zoom Out");
    myControls.add(zoomOutCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame g = cmp.getGame();
    GameOptions gameOptions = cmp.getOptions();
    boolean justClosed = closeCtrl.isJustOff();
    MapDrawer mapDrawer = g.getMapDrawer();
    mapDrawer.setToggled(!justClosed);
    ManiInputManager im = cmp.getInputMan();
    if (justClosed) {
      im.setScreen(cmp, g.getScreens().mainScreen);
    }
    boolean zoomIn = zoomInCtrl.isJustOff();
    if (zoomIn || zoomOutCtrl.isJustOff()) {
      mapDrawer.changeZoom(zoomIn);
    }
    float mapZoom = mapDrawer.getZoom();
    zoomInCtrl.setEnabled(mapZoom != MapDrawer.MIN_ZOOM);
    zoomOutCtrl.setEnabled(mapZoom != MapDrawer.MAX_ZOOM);
    ShipUiControl sc = g.getScreens().mainScreen.shipControl;
    if (sc instanceof ShipMouseControl) sc.update(cmp, true);
    Boolean scrolledUp = im.getScrolledUp();
    if (scrolledUp != null) {
      if (scrolledUp) {
        zoomOutCtrl.maybeFlashPressed(gameOptions.getKeyZoomOut());
      } else {
        zoomInCtrl.maybeFlashPressed(gameOptions.getKeyZoomIn());
      }
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
