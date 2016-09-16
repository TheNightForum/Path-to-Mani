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

package com.pathtomani.game.input;

import com.pathtomani.common.ManiMath;
import com.pathtomani.game.item.ManiItem;
import com.pathtomani.game.ship.ManiShip;
import com.pathtomani.game.ship.ShipAbility;

public class AbilityUpdater {
  private final float myAbilityUseStartPerc;
  private final int myChargesToKeep;

  private boolean myAbility;

  public AbilityUpdater() {
    myAbilityUseStartPerc = ManiMath.rnd(.3f, .7f);
    myChargesToKeep = ManiMath.intRnd(1, 2);
  }

  public void update(ManiShip ship, ManiShip nearestEnemy) {
    myAbility = false;
    if (nearestEnemy == null) return;
    ShipAbility ability = ship.getAbility();
    if (ability == null) return;
    if (ship.getHull().config.getMaxLife() * myAbilityUseStartPerc < ship.getLife()) return;
    ManiItem ex = ability.getConfig().getChargeExample();
    if (ex != null) {
      if (ship.getItemContainer().count(ex) <= myChargesToKeep) return;
    }
    if (ability.getRadius() < nearestEnemy.getPosition().dst(ship.getPosition())) return;
    myAbility = true;
  }

  public boolean isAbility() {
    return myAbility;
  }
}
