/*
 * Copyright 2017 TheNightForum
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
package old.tnf.ptm.game.screens;

import com.badlogic.gdx.math.Rectangle;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.ui.PtmUiScreen;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.common.PtmColor;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.ship.hulls.HullConfig;
import old.tnf.ptm.menu.MenuLayout;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class TalkScreen implements PtmUiScreen {
    static final float MAX_TALK_DIST = 1f;

    private final List<PtmUiControl> controls = new ArrayList<>();
    public final PtmUiControl buyControl;
    public final PtmUiControl closeControl;
    private final PtmUiControl sellControl;
    private final PtmUiControl shipsControl;
    private final PtmUiControl hireControl;

    private final Rectangle bg;
    private PtmShip target;

    TalkScreen(MenuLayout menuLayout, GameOptions gameOptions) {
        sellControl = new PtmUiControl(menuLayout.buttonRect(-1, 0), true, gameOptions.getKeySellMenu());
        sellControl.setDisplayName("Sell");
        controls.add(sellControl);

        buyControl = new PtmUiControl(menuLayout.buttonRect(-1, 1), true, gameOptions.getKeyBuyMenu());
        buyControl.setDisplayName("Buy");
        controls.add(buyControl);

        shipsControl = new PtmUiControl(menuLayout.buttonRect(-1, 2), true, gameOptions.getKeyChangeShipMenu());
        shipsControl.setDisplayName("Change Ship");
        controls.add(shipsControl);

        hireControl = new PtmUiControl(menuLayout.buttonRect(-1, 3), true, gameOptions.getKeyHireShipMenu());
        hireControl.setDisplayName("Hire");
        controls.add(hireControl);

        closeControl = new PtmUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
        closeControl.setDisplayName("Close");
        controls.add(closeControl);

        bg = menuLayout.bg(-1, 0, 5);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (clickedOutside) {
            closeControl.maybeFlashPressed(ptmApplication.getOptions().getKeyClose());
            return;
        }
        PtmGame g = ptmApplication.getGame();
        PtmShip hero = g.getHero();
        PtmInputManager inputMan = ptmApplication.getInputMan();
        if (closeControl.isJustOff() || isTargetFar(hero)) {
            inputMan.setScreen(ptmApplication, g.getScreens().mainScreen);
            return;
        }

        boolean station = target.getHull().config.getType() == HullConfig.Type.STATION;
        shipsControl.setEnabled(station);
        hireControl.setEnabled(station);

        InventoryScreen is = g.getScreens().inventoryScreen;
        boolean sell = sellControl.isJustOff();
        boolean buy = buyControl.isJustOff();
        boolean sellShips = shipsControl.isJustOff();
        boolean hire = hireControl.isJustOff();
        if (sell || buy || sellShips || hire) {
            is.setOperations(sell ? is.sellItems : buy ? is.buyItems : sellShips ? is.changeShip : is.hireShips);
            inputMan.setScreen(ptmApplication, g.getScreens().mainScreen);
            inputMan.addScreen(ptmApplication, is);
        }
    }

    boolean isTargetFar(PtmShip hero) {
        if (hero == null || target == null || target.getLife() <= 0) {
            return true;
        }
        float dst = target.getPosition().dst(hero.getPosition()) - hero.getHull().config.getApproxRadius() - target.getHull().config.getApproxRadius();
        return MAX_TALK_DIST < dst;
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bg, PtmColor.UI_BG);
    }

    @Override
    public boolean reactsToClickOutside() {
        return true;
    }

    @Override
    public boolean isCursorOnBg(PtmInputManager.InputPointer inputPointer) {
        return bg.contains(inputPointer.x, inputPointer.y);
    }

    public PtmShip getTarget() {
        return target;
    }

    public void setTarget(PtmShip target) {
        this.target = target;
    }
}
