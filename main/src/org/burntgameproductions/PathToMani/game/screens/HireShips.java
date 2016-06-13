/*
 * Copyright 2016 MovingBlocks
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

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.Faction;
import org.burntgameproductions.PathToMani.game.input.AiPilot;
import org.burntgameproductions.PathToMani.game.input.Guardian;
import org.burntgameproductions.PathToMani.game.item.ItemContainer;
import org.burntgameproductions.PathToMani.game.item.MercItem;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.ship.FarShip;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ShipConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class HireShips implements InventoryOperations {

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBuyCtrl;

  public HireShips(InventoryScreen inventoryScreen, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myBuyCtrl = new ManiUiControl(inventoryScreen.itemCtrl(0), true, gameOptions.getKeyHireShip());
    myBuyCtrl.setDisplayName("Hire");
    myControls.add(myBuyCtrl);
  }

  @Override
  public ItemContainer getItems(ManiGame game) {
    return game.getScreens().talkScreen.getTarget().getTradeContainer().getMercs();
  }

  @Override
  public boolean isUsing(ManiGame game, ManiItem item) {
    return false;
  }

  @Override
  public float getPriceMul() {
    return 1;
  }

  @Override
  public String getHeader() {
    return "Mercenaries:";
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame game = cmp.getGame();
    InventoryScreen is = game.getScreens().inventoryScreen;
    ManiShip hero = game.getHero();
    TalkScreen talkScreen = game.getScreens().talkScreen;
    if (talkScreen.isTargetFar(hero)) {
      cmp.getInputMan().setScreen(cmp, game.getScreens().mainScreen);
      return;
    }
    ManiItem selItem = is.getSelectedItem();
    boolean enabled = selItem != null && hero.getMoney() >= selItem.getPrice();
    myBuyCtrl.setDisplayName(enabled ? "Hire" : "---");
    myBuyCtrl.setEnabled(enabled);
    if (!enabled) return;
    if (myBuyCtrl.isJustOff()) {
      boolean hired = hireShip(game, hero, (MercItem) selItem);
      if (hired) hero.setMoney(hero.getMoney() - selItem.getPrice());
    }
  }

  private boolean hireShip(ManiGame game, ManiShip hero, MercItem selected) {
    ShipConfig config = selected.getConfig();
    Guardian dp = new Guardian(game, config.hull, hero.getPilot(), hero.getPosition(), hero.getHull().config, SolMath.rnd(180));
    AiPilot pilot = new AiPilot(dp, true, Faction.LAANI, false, "Merc", Const.AI_DET_DIST);
    Vector2 pos = getPos(game, hero, config.hull);
    if (pos == null) return false;
    FarShip merc = game.getShipBuilder().buildNewFar(game, pos, new Vector2(), 0, 0, pilot, config.items, config.hull, null, true, config.money, null, true);
    game.getObjMan().addFarObjNow(merc);
    return true;
  }

  private Vector2 getPos(ManiGame game, ManiShip hero, HullConfig hull) {
    Vector2 pos = new Vector2();
    float dist = hero.getHull().config.getApproxRadius() + Guardian.DIST + hull.getApproxRadius();
    Vector2 heroPos = hero.getPosition();
    Planet np = game.getPlanetMan().getNearestPlanet();
    boolean nearGround = np.isNearGround(heroPos);
    float fromPlanet = SolMath.angle(np.getPos(), heroPos);
    for (int i = 0; i < 50; i++) {
      float relAngle;
      if (nearGround) {
        relAngle = fromPlanet;
      } else {
        relAngle = SolMath.rnd(180);
      }
      SolMath.fromAl(pos, relAngle, dist);
      pos.add(heroPos);
      if (game.isPlaceEmpty(pos, false)) return pos;
      dist += Guardian.DIST;
    }
    return null;
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

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
    cmp.getGame();
  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
