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

package com.tnf.ptm.handler.input;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.PtmGame;
import com.tnf.ptm.entities.planet.Planet;
import com.tnf.ptm.entities.ship.PtmShip;

public class Mover {
    public static final float MIN_MOVE_AAD = 2f;
    public static final float MIN_ANGLE_TO_ACC = 5f;
    public static final float MIN_PLANET_MOVE_AAD = 2f;
    public static final float MAX_ABS_SPD_DEV = .1f;
    public static final float MAX_REL_SPD_DEV = .05f;
    private final BigObjAvoider myBigObjAvoider;
    private final SmallObjAvoider mySmallObjAvoider;
    private boolean myUp;
    private boolean myLeft;
    private boolean myRight;
    private Vector2 myDesiredSpd;

    public Mover() {
        myBigObjAvoider = new BigObjAvoider();
        mySmallObjAvoider = new SmallObjAvoider();
        myDesiredSpd = new Vector2();
    }

    public static Boolean needsToTurn(float angle, float destAngle, float rotSpd, float rotAcc, float allowedAngleDiff) {
        if (PtmMath.angleDiff(destAngle, angle) < allowedAngleDiff || rotAcc == 0) {
            return null;
        }

        float breakWay = rotSpd * rotSpd / rotAcc / 2;
        float angleAfterBreak = angle + breakWay * PtmMath.toInt(rotSpd > 0);
        float relAngle = PtmMath.norm(angle - destAngle);
        float relAngleAfterBreak = PtmMath.norm(angleAfterBreak - destAngle);
        if (relAngle > 0 == relAngleAfterBreak > 0) {
            return relAngle < 0;
        }
        return relAngle > 0;
    }

    public void update(PtmGame game, PtmShip ship, Vector2 dest, Planet np,
                       float maxIdleDist, boolean hasEngine, boolean avoidBigObjs, float desiredSpdLen, boolean stopNearDest,
                       Vector2 destSpd) {
        myUp = false;
        myLeft = false;
        myRight = false;

        if (!hasEngine || dest == null) {
            return;
        }

        Vector2 shipPos = ship.getPosition();

        float toDestLen = shipPos.dst(dest);

        if (toDestLen < maxIdleDist) {
            if (!stopNearDest) {
                return;
            }
            myDesiredSpd.set(destSpd);
        } else {
            updateDesiredSpd(game, ship, dest, toDestLen, stopNearDest, np, avoidBigObjs, desiredSpdLen, destSpd);
        }

        Vector2 shipSpd = ship.getSpd();
        float spdDeviation = shipSpd.dst(myDesiredSpd);
        if (spdDeviation < MAX_ABS_SPD_DEV || spdDeviation < MAX_REL_SPD_DEV * shipSpd.len()) {
            return;
        }

        float shipAngle = ship.getAngle();
        float rotSpd = ship.getRotSpd();
        float rotAcc = ship.getRotAcc();

        float desiredAngle = PtmMath.angle(shipSpd, myDesiredSpd);
        float angleDiff = PtmMath.angleDiff(desiredAngle, shipAngle);
        myUp = angleDiff < MIN_ANGLE_TO_ACC;
        Boolean ntt = needsToTurn(shipAngle, desiredAngle, rotSpd, rotAcc, MIN_MOVE_AAD);
        if (ntt != null) {
            if (ntt) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    private void updateDesiredSpd(PtmGame game, PtmShip ship, Vector2 dest, float toDestLen, boolean stopNearDest,
                                  Planet np, boolean avoidBigObjs, float desiredSpdLen, Vector2 destSpd) {
        float toDestAngle = getToDestAngle(game, ship, dest, avoidBigObjs, np);
        if (stopNearDest) {
            float tangentSpd = PtmMath.project(ship.getSpd(), toDestAngle);
            float turnWay = tangentSpd * ship.calcTimeToTurn(toDestAngle + 180);
            float breakWay = tangentSpd * tangentSpd / ship.getAcc() / 2;
            boolean needsToBreak = toDestLen < .5f * tangentSpd + turnWay + breakWay;
            if (needsToBreak) {
                myDesiredSpd.set(destSpd);
                return;
            }
        }
        PtmMath.fromAl(myDesiredSpd, toDestAngle, desiredSpdLen);
    }

    public void rotateOnIdle(PtmShip ship, Planet np, Vector2 dest, boolean stopNearDest, float maxIdleDist) {
        if (isActive() || dest == null) {
            return;
        }
        Vector2 shipPos = ship.getPosition();
        float shipAngle = ship.getAngle();
        float toDestLen = shipPos.dst(dest);
        float desiredAngle;
        float allowedAngleDiff;
        boolean nearFinalDest = stopNearDest && toDestLen < maxIdleDist;
        float dstToPlanet = np.getPos().dst(shipPos);
        if (nearFinalDest) {
            if (np.getFullHeight() < dstToPlanet) {
                return; // stopping in space, don't care of angle
            }
            // stopping on planet
            desiredAngle = PtmMath.angle(np.getPos(), shipPos);
            allowedAngleDiff = MIN_PLANET_MOVE_AAD;
        } else {
            // flying somewhere
            if (dstToPlanet < np.getFullHeight() + Const.ATM_HEIGHT) {
                return; // near planet, don't care of angle
            }
            desiredAngle = PtmMath.angle(ship.getSpd());
            allowedAngleDiff = MIN_MOVE_AAD;
        }

        Boolean ntt = needsToTurn(shipAngle, desiredAngle, ship.getRotSpd(), ship.getRotAcc(), allowedAngleDiff);
        if (ntt != null) {
            if (ntt) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    private float getToDestAngle(PtmGame game, PtmShip ship, Vector2 dest, boolean avoidBigObjs, Planet np) {
        Vector2 shipPos = ship.getPosition();
        float toDestAngle = PtmMath.angle(shipPos, dest);
        if (avoidBigObjs) {
            toDestAngle = myBigObjAvoider.avoid(game, shipPos, dest, toDestAngle);
        }
        toDestAngle = mySmallObjAvoider.avoid(game, ship, toDestAngle, np);
        return toDestAngle;
    }

    public boolean isUp() {
        return myUp;
    }

    public boolean isLeft() {
        return myLeft;
    }

    public boolean isRight() {
        return myRight;
    }

    public boolean isActive() {
        return myUp || myLeft || myRight;
    }

    public BigObjAvoider getBigObjAvoider() {
        return myBigObjAvoider;
    }
}
