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

import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.PtmApplication;
import old.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.ShipConfig;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.input.AiPilot;
import old.tnf.ptm.game.item.ItemContainer;
import old.tnf.ptm.game.item.PtmItem;
import old.tnf.ptm.game.planet.Planet;
import old.tnf.ptm.game.ship.FarShip;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.game.ship.hulls.HullConfig;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.GameOptions;
import old.tnf.ptm.game.Faction;
import old.tnf.ptm.game.input.Guardian;
import old.tnf.ptm.game.item.MercItem;

import java.util.ArrayList;
import java.util.List;

public class HireShips implements InventoryOperations {
    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl hireControl;

    HireShips(InventoryScreen inventoryScreen, GameOptions gameOptions) {
        hireControl = new PtmUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyHireShip());
        hireControl.setDisplayName("Hire");
        controls.add(hireControl);
    }

    @Override
    public ItemContainer getItems(PtmGame game) {
        return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
    }

    @Override
    public String getHeader() {
        return "Mercenaries:";
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        PtmGame game = ptmApplication.getGame();
        InventoryScreen is = game.getScreens().inventoryScreen;
        PtmShip hero = game.getHero();
        TalkScreen talkScreen = game.getScreens().talkScreen;
        if (talkScreen.isTargetFar(hero)) {
            ptmApplication.getInputMan().setScreen(ptmApplication, game.getScreens().mainScreen);
            return;
        }
        PtmItem selItem = is.getSelectedItem();
        boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
        hireControl.setDisplayName(enabled ? "Hire" : "---");
        hireControl.setEnabled(enabled);
        if (!enabled) {
            return;
        }
        if (hireControl.isJustOff()) {
            boolean hired = hireShip(game, hero, (MercItem) selItem);
            if (hired) {
                hero.setMoney(hero.getMoney() - selItem.getPrice());
            }
        }
    }

    private boolean hireShip(PtmGame game, PtmShip hero, MercItem selected) {
        ShipConfig config = selected.getConfig();
        Guardian dp = new Guardian(game, config.hull, hero.getPilot(), hero.getPosition(), hero.getHull().config, PtmMath.rnd(180));
        AiPilot pilot = new AiPilot(dp, true, Faction.LAANI, false, "Merc", Const.AI_DET_DIST);
        Vector2 pos = getPos(game, hero, config.hull);
        if (pos == null) {
            return false;
        }
        FarShip merc = game.getShipBuilder().buildNewFar(game, pos, new Vector2(), 0, 0, pilot, config.items, config.hull, null, true, config.money, null, true);
        game.getObjMan().addFarObjNow(merc);
        return true;
    }

    private Vector2 getPos(PtmGame game, PtmShip hero, HullConfig hull) {
        Vector2 pos = new Vector2();
        float dist = hero.getHull().config.getApproxRadius() + Guardian.DIST + hull.getApproxRadius();
        Vector2 heroPos = hero.getPosition();
        Planet np = game.getPlanetMan().getNearestPlanet();
        boolean nearGround = np.isNearGround(heroPos);
        float fromPlanet = PtmMath.angle(np.getPos(), heroPos);
        for (int i = 0; i < 50; i++) {
            float relAngle;
            if (nearGround) {
                relAngle = fromPlanet;
            } else {
                relAngle = PtmMath.rnd(180);
            }
            PtmMath.fromAl(pos, relAngle, dist);
            pos.add(heroPos);
            if (game.isPlaceEmpty(pos, false)) {
                return pos;
            }
            dist += Guardian.DIST;
        }
        return null;
    }
}
