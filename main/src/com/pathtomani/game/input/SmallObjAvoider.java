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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.ManiObject;
import com.pathtomani.game.planet.Planet;
import com.pathtomani.game.ship.ManiShip;

public class SmallObjAvoider {
  public static final float MANEUVER_TIME = 2f;
  public static final float MIN_RAYCAST_LEN = .5f;
  private final RayCastCallback myRayBack;
  private ManiShip myShip;
  private boolean myCollided;
  private final Vector2 myDest;

  public SmallObjAvoider() {
    myRayBack = new MyRayBack();
    myDest = new Vector2();
  }

  public float avoid(ManiGame game, ManiShip ship, float toDestAngle, Planet np) {
    myShip = ship;
    Vector2 shipPos = ship.getPosition();
    float shipSpdLen = ship.getSpd().len();
    float ttt = ship.calcTimeToTurn(toDestAngle + 45);
    float raycastLen = shipSpdLen * (ttt + MANEUVER_TIME);
    if (raycastLen < MIN_RAYCAST_LEN) raycastLen = MIN_RAYCAST_LEN;

    ManiMath.fromAl(myDest, toDestAngle, raycastLen);
    myDest.add(shipPos);
    myCollided = false;
    World w = game.getObjMan().getWorld();
    w.rayCast(myRayBack, shipPos, myDest);
    if (!myCollided) return toDestAngle;

    toDestAngle += 45;
    ManiMath.fromAl(myDest, toDestAngle, raycastLen);
    myDest.add(shipPos);
    myCollided = false;
    w.rayCast(myRayBack, shipPos, myDest);
    if (!myCollided) return toDestAngle;

    toDestAngle -= 90;
    ManiMath.fromAl(myDest, toDestAngle, raycastLen);
    myDest.add(shipPos);
    myCollided = false;
    w.rayCast(myRayBack, shipPos, myDest);
    if (!myCollided) return toDestAngle;

    if (np.getFullHeight() < np.getPos().dst(shipPos)) return toDestAngle - 45;
    return ManiMath.angle(np.getPos(), shipPos);
  }

  private class MyRayBack implements RayCastCallback {
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      ManiObject o = (ManiObject) fixture.getBody().getUserData();
      if (myShip == o) {
        return -1;
      }
      myCollided = true;
      return 0;
    }
  }
}
