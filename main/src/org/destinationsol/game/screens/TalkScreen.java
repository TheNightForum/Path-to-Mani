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

package org.destinationsol.game.screens;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.GameOptions;
import org.destinationsol.ManiApplication;
import org.destinationsol.common.ManiColor;
import org.destinationsol.game.ManiGame;
import org.destinationsol.game.ship.ManiShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.ManiUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class TalkScreen implements ManiUiScreen {

  public static final float MAX_TALK_DIST = 1f;
  private final List<SolUiControl> myControls;
  private final SolUiControl mySellCtrl;
  public final SolUiControl buyCtrl;
  private final SolUiControl myShipsCtrl;
  private final SolUiControl myHireCtrl;
  private final Rectangle myBg;
  public final SolUiControl closeCtrl;
  private ManiShip myTarget;

  public TalkScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<SolUiControl>();

    mySellCtrl = new SolUiControl(menuLayout.buttonRect(-1, 0), true, gameOptions.getKeySellMenu());
    mySellCtrl.setDisplayName("Sell");
    myControls.add(mySellCtrl);

    buyCtrl = new SolUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyBuyMenu());
    buyCtrl.setDisplayName("Buy");
    myControls.add(buyCtrl);

    myShipsCtrl = new SolUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyChangeShipMenu());
    myShipsCtrl.setDisplayName("Change Ship");
    myControls.add(myShipsCtrl);

    myHireCtrl = new SolUiControl(menuLayout.buttonRect(-1, 3), true, gameOptions.getKeyHireShipMenu());
    myHireCtrl.setDisplayName("Hire");
    myControls.add(myHireCtrl);

    closeCtrl = new SolUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    closeCtrl.setDisplayName("Close");
    myControls.add(closeCtrl);

    myBg = menuLayout.bg(-1, 0, 5);
  }

  @Override
  public List<SolUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, SolInputManager.Ptr[] ptrs, boolean clickedOutside) {
    if (clickedOutside) {
      closeCtrl.maybeFlashPressed(cmp.getOptions().getKeyClose());
      return;
    }
    ManiGame g = cmp.getGame();
    ManiShip hero = g.getHero();
    SolInputManager inputMan = cmp.getInputMan();
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
  public boolean isCursorOnBg(SolInputManager.Ptr ptr) {
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
