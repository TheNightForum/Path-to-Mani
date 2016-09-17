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

import com.badlogic.gdx.math.Rectangle;
import com.pathtomani.common.GameOptions;
import com.pathtomani.ManiApplication;
import com.pathtomani.gfx.ManiColor;
import com.pathtomani.game.ManiGame;
import com.pathtomani.entities.ship.ManiShip;
import com.pathtomani.entities.ship.hulls.HullConfig;
import com.pathtomani.screens.controllers.ManiInputManager;
import com.pathtomani.screens.controllers.ManiUiScreen;
import com.pathtomani.screens.controllers.UiDrawer;
import com.pathtomani.screens.menu.MenuLayout;
import com.pathtomani.screens.controllers.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class TalkScreen implements ManiUiScreen {

  public static final float MAX_TALK_DIST = 1f;
  private final List<ManiUiControl> myControls;
  private final ManiUiControl mySellCtrl;
  public final ManiUiControl buyCtrl;
  private final ManiUiControl myShipsCtrl;
  private final ManiUiControl myHireCtrl;
  private final Rectangle myBg;
  public final ManiUiControl closeCtrl;
  private ManiShip myTarget;

  public TalkScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    mySellCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 0), true, gameOptions.getKeySellMenu());
    mySellCtrl.setDisplayName("Sell");
    myControls.add(mySellCtrl);

    buyCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyBuyMenu());
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);

    myShipsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyChangeShipMenu());
    myShipsCtrl.setDisplayName("Change Ship");
    myControls.add(myShipsCtrl);

    myHireCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, gameOptions.getKeyHireShipMenu());
    myHireCtrl.setDisplayName("Hire");
    myControls.add(myHireCtrl);

    closeCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);

    myBg = menuLayout.bg(-1, 0, 5);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (clickedOutside) {
      closeCtrl.maybeFlashPressed(cmp.getOptions().getKeyClose());
      return;
    }
    ManiGame g = cmp.getGame();
    ManiShip hero = g.getHero();
    ManiInputManager inputMan = cmp.getInputMan();
    if (closeCtrl.isJustOff() || isTargetFar(hero))
    {
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      return;
    }

    boolean station = myTarget.getHull().config.getType() == HullConfig.Type.STATION;
    myShipsCtrl.setEnabled(station);
    myHireCtrl.setEnabled(station);

    InventoryScreen is = g.getScreens().inventoryScreen;
    boolean sell = mySellCtrl.isJustOff();
    boolean buy = buyCtrl.isJustOff();
    boolean sellShips = myShipsCtrl.isJustOff();
    boolean hire = myHireCtrl.isJustOff();
    if (sell || buy || sellShips || hire) {
      is.setOperations(sell ? is.sellItems : buy ? is.buyItems : sellShips ? is.changeShip : is.hireShips);
      inputMan.setScreen(cmp, g.getScreens().mainScreen);
      inputMan.addScreen(cmp, is);
    }
  }

  public boolean isTargetFar(ManiShip hero) {
    if (hero == null || myTarget == null || myTarget.getLife() <= 0) return true;
    float dst = myTarget.getPosition().dst(hero.getPosition()) - hero.getHull().config.getApproxRadius() - myTarget.getHull().config.getApproxRadius();
    return MAX_TALK_DIST < dst;
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
    uiDrawer.draw(myBg, ManiColor.UI_BG);
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return true;
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return myBg.contains(ptr.x, ptr.y);
  }

  @Override
  public void onAdd(ManiApplication cmp) {
  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }

  public void setTarget(ManiShip target) {
    myTarget = target;
  }

  public ManiShip getTarget() {
    return myTarget;
  }
}
