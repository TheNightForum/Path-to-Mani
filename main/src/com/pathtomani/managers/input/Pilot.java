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

package com.pathtomani.managers.input;

import com.pathtomani.game.ManiGame;
import com.pathtomani.entities.ship.FarShip;
import com.pathtomani.entities.ship.ManiShip;
import com.pathtomani.game.Faction;

public interface Pilot {
  void update(ManiGame game, ManiShip ship, ManiShip nearestEnemy);
  boolean isUp();
  boolean isLeft();
  boolean isRight();
  boolean isShoot();
  boolean isShoot2();
  boolean collectsItems();
  boolean isAbility();
  Faction getFaction();
  boolean shootsAtObstacles();
  float getDetectionDist();
  String getMapHint();
  void updateFar(ManiGame game, FarShip farShip);
  String toDebugString();
  boolean isPlayer();

  public static final class Utils {
    public static boolean isIdle(Pilot p) {
      return !(p.isUp() || p.isShoot() || p.isShoot2() || p.isAbility());
    }
  }
}
