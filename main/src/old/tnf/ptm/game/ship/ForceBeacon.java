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

package old.tnf.ptm.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.Faction;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.dra.Dra;
import old.tnf.ptm.game.particle.ParticleSrc;
import old.tnf.ptm.game.input.Pilot;

import java.util.List;

public class ForceBeacon {

    public static final float MAX_PULL_DIST = .7f;
    private final Vector2 myRelPos;
    private final Vector2 myPrevPos;
    private final ParticleSrc myEffect;

    public ForceBeacon(PtmGame game, Vector2 relPos, Vector2 basePos, Vector2 baseSpd) {
        myRelPos = relPos;
        myEffect = game.getSpecialEffects().buildForceBeacon(.6f, game, relPos, basePos, baseSpd);
        myEffect.setWorking(true);
        myPrevPos = new Vector2();
    }

    public static PtmShip pullShips(PtmGame game, PtmObject owner, Vector2 ownPos, Vector2 ownSpd, Faction faction,
                                    float maxPullDist) {
        PtmShip res = null;
        float minLen = Float.MAX_VALUE;
        List<PtmObject> objs = game.getObjMan().getObjs();
        for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
            PtmObject o = objs.get(i);
            if (o == owner) {
                continue;
            }
            if (!(o instanceof PtmShip)) {
                continue;
            }
            PtmShip ship = (PtmShip) o;
            Pilot pilot = ship.getPilot();
            if (pilot.isUp() || pilot.isLeft() || pilot.isRight()) {
                continue;
            }
            if (game.getFactionMan().areEnemies(faction, pilot.getFaction())) {
                continue;
            }
            Vector2 toMe = PtmMath.distVec(ship.getPosition(), ownPos);
            float toMeLen = toMe.len();
            if (toMeLen < maxPullDist) {
                if (toMeLen > 1) {
                    toMe.scl(1 / toMeLen);
                }
                if (ownSpd != null) {
                    toMe.add(ownSpd);
                }
                ship.getHull().getBody().setLinearVelocity(toMe);
                game.getSoundManager().play(game, game.getSpecialSounds().forceBeaconWork, null, ship);
                if (toMeLen < minLen) {
                    res = ship;
                    minLen = toMeLen;
                }
            }
            PtmMath.free(toMe);
        }
        return res;
    }

    public void collectDras(List<Dra> dras) {
        dras.add(myEffect);
    }

    public void update(PtmGame game, Vector2 basePos, float baseAngle, PtmShip ship) {
        Vector2 pos = PtmMath.toWorld(myRelPos, baseAngle, basePos);
        Vector2 spd = PtmMath.distVec(myPrevPos, pos).scl(1 / game.getTimeStep());
        Faction faction = ship.getPilot().getFaction();
        pullShips(game, ship, pos, spd, faction, MAX_PULL_DIST);
        PtmMath.free(spd);
        myPrevPos.set(pos);
        PtmMath.free(pos);
    }
}
