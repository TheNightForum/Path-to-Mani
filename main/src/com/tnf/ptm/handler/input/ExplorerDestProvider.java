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
import com.tnf.ptm.entities.planet.PtmSystem;
import com.tnf.ptm.entities.ship.PtmShip;
import com.tnf.ptm.entities.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Flies from planet to planet, stays on the planet ground or in atmosphere for some time, then flies to the next planet
 */
public class ExplorerDestProvider implements MoveDestProvider {
    public static final int MAX_AWAIT_ON_PLANET = 30;
    public static final int LAST_PLANETS_TO_AVOID = 2;
    private final Vector2 myDest;
    private final boolean myAggressive;
    private final float myDesiredSpdLen;
    private final PtmSystem mySys;
    private Vector2 myRelDest;
    private Planet myPlanet;
    private float myAwaitOnPlanet;
    private boolean myDestIsLanding;
    private Vector2 myDestSpd;

    public ExplorerDestProvider(PtmGame game, Vector2 pos, boolean aggressive, HullConfig config, PtmSystem sys) {
        mySys = sys;
        myDest = new Vector2();
        float minDst = Float.MAX_VALUE;
        ArrayList<Planet> planets = mySys.getPlanets();
        for (int i = 0, sz = allowedSz(); i < sz; i++) {
            Planet p = planets.get(i);
            float dst = p.getPos().dst(pos);
            if (dst < minDst) {
                minDst = dst;
                myPlanet = p;
            }
        }
        calcRelDest(config);
        myAwaitOnPlanet = MAX_AWAIT_ON_PLANET;
        myAggressive = aggressive;
        myDesiredSpdLen = config.getType() == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
        myDestSpd = new Vector2();
    }

    private int allowedSz() {
        int sz = mySys.getPlanets().size();
        if (!mySys.getConfig().hard) {
            sz -= LAST_PLANETS_TO_AVOID;
        }
        return sz;
    }

    private void calcRelDest(HullConfig hullConfig) {
        List<Vector2> lps = myPlanet.getLandingPlaces();
        if (lps.size() > 0) {
            myRelDest = new Vector2(PtmMath.elemRnd(lps));
            float len = myRelDest.len();
            float aboveGround = hullConfig.getType() == HullConfig.Type.BIG ? Const.ATM_HEIGHT * .75f : .75f * hullConfig.getSize();
            myRelDest.scl((len + aboveGround) / len);
            myDestIsLanding = true;
        } else {
            myRelDest = new Vector2();
            PtmMath.fromAl(myRelDest, PtmMath.rnd(180), myPlanet.getGroundHeight() + .3f * Const.ATM_HEIGHT);
            myDestIsLanding = false;
        }
    }

    @Override
    public Vector2 getDest() {
        return myDest;
    }

    @Override
    public boolean shouldStopNearDest() {
        return true;
    }

    @Override
    public void update(PtmGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, PtmShip nearestEnemy) {
        if (myDest.dst(shipPos) < maxIdleDist) {
            if (myAwaitOnPlanet > 0) {
                myAwaitOnPlanet -= game.getTimeStep();
            } else {
                ArrayList<Planet> ps = mySys.getPlanets();
                int pIdx = PtmMath.intRnd(allowedSz());
                myPlanet = ps.get(pIdx);
                calcRelDest(hullConfig);
                myAwaitOnPlanet = MAX_AWAIT_ON_PLANET;
            }
        }

        if (!myDestIsLanding && !myPlanet.getLandingPlaces().isEmpty()) {
            calcRelDest(hullConfig);
        }

        PtmMath.toWorld(myDest, myRelDest, myPlanet.getAngle(), myPlanet.getPos(), false);
        myPlanet.calcSpdAtPos(myDestSpd, myDest);
    }

    @Override
    public Boolean shouldManeuver(boolean canShoot, PtmShip nearestEnemy, boolean nearGround) {
        if (myAggressive && canShoot) {
            return true;
        }
        return null;
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
        return myDesiredSpdLen;
    }
}
