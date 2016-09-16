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

import com.badlogic.gdx.math.Vector2;
import com.pathtomani.game.BeaconHandler;
import com.pathtomani.game.ManiGame;
import com.pathtomani.entities.ship.ManiShip;
import com.pathtomani.entities.ship.hulls.HullConfig;
import com.pathtomani.common.Const;

public class BeaconDestProvider implements MoveDestProvider {
  public static final float STOP_AWAIT = .1f;
  private final Vector2 myDest;

  private Boolean myShouldManeuver;
  private boolean myShouldStopNearDest;
  private Vector2 myDestSpd;

  public BeaconDestProvider() {
    myDest = new Vector2();
    myDestSpd = new Vector2();
  }

  @Override
  public void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy) {
    BeaconHandler bh = game.getBeaconHandler();
    myDest.set(bh.getPos());
    myShouldManeuver = null;
    BeaconHandler.Action a = bh.getCurrAction();
    if (nearestEnemy != null && a == BeaconHandler.Action.ATTACK) {
      if (shipPos.dst(myDest) < shipPos.dst(nearestEnemy.getPosition()) + .1f) myShouldManeuver = true;
    }
    myShouldStopNearDest = STOP_AWAIT < game.getTime() - bh.getClickTime();
    myDestSpd.set(bh.getSpd());
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, ManiShip nearestEnemy, boolean nearGround) {
    return myShouldManeuver;
  }

  @Override
  public Vector2 getDestSpd() {
    return myDestSpd;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return true;
  }

  @Override
  public float getDesiredSpdLen() {
    return Const.MAX_MOVE_SPD;
  }

  @Override
  public boolean shouldStopNearDest() {
    return myShouldStopNearDest;
  }
}
