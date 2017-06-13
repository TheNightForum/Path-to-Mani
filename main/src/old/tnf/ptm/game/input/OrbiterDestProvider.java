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

package old.tnf.ptm.game.input;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.planet.Planet;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.game.ship.hulls.HullConfig;

/**
 * Flies in the planet orbit
 */
public class OrbiterDestProvider implements MoveDestProvider {
    private final Planet myPlanet;
    private final float myDesiredSpd;
    private final float myHeight;
    private final boolean myCw;
    private final Vector2 myDest;

    public OrbiterDestProvider(Planet planet, float height, boolean cw) {
        myPlanet = planet;
        myHeight = height;
        myCw = cw;
        myDesiredSpd = PtmMath.sqrt(myPlanet.getGravConst() / myHeight);
        myDest = new Vector2();
    }

    @Override
    public Vector2 getDest() {
        return myDest;
    }

    @Override
    public boolean shouldAvoidBigObjs() {
        return false;
    }

    @Override
    public float getDesiredSpdLen() {
        return myDesiredSpd;
    }

    @Override
    public boolean shouldStopNearDest() {
        return false;
    }

    @Override
    public void update(PtmGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, PtmShip nearestEnemy) {
        Vector2 pPos = myPlanet.getPos();
        float destAngle = PtmMath.angle(pPos, shipPos) + 5 * PtmMath.toInt(myCw);
        PtmMath.fromAl(myDest, destAngle, myHeight);
        myDest.add(pPos);
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, PtmShip nearestEnemy, boolean nearGround) {
        return null;
    }

    @Override
    public Vector2 getDestSpd() {
        return Vector2.Zero;
    }
}
