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

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ShipConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.planet.PlanetBind;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

public class StillGuard implements MoveDestProvider {

  private final PlanetBind myPlanetBind;
  private final float myDesiredSpdLen;
  private Vector2 myDest;
  private Vector2 myDestSpd;

  public StillGuard(Vector2 target, ManiGame game, ShipConfig sc) {
    myDest = new Vector2(target);
    myPlanetBind = PlanetBind.tryBind(game, myDest, 0);
    myDesiredSpdLen = sc.hull.getType() == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
    myDestSpd = new Vector2();
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return myPlanetBind != null;
  }

  @Override
  public float getDesiredSpdLen() {
    return myDesiredSpdLen;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy) {
    if (myPlanetBind != null) {
      Vector2 diff = SolMath.getVec();
      myPlanetBind.setDiff(diff, myDest, false);
      myDest.add(diff);
      SolMath.free(diff);
      myPlanetBind.getPlanet().calcSpdAtPos(myDestSpd, myDest);
    }
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, ManiShip nearestEnemy, boolean nearGround) {
    return true;
  }

  @Override
  public Vector2 getDestSpd() {
    return myDestSpd;
  }
}
