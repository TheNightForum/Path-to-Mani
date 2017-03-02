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
package com.tnf.ptm.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.SolMath;
import com.tnf.ptm.game.FarObj;
import com.tnf.ptm.game.SolGame;
import com.tnf.ptm.game.SolObject;
import com.tnf.ptm.game.dra.Dra;
import com.tnf.ptm.game.dra.DraMan;

import java.util.List;

public class FarPlanetSprites implements FarObj {
    private final Planet myPlanet;
    private final float myDist;
    private final List<Dra> myDras;
    private final float myRadius;
    private final float myToPlanetRotSpd;
    private float myRelAngleToPlanet;
    private Vector2 myPos;

    public FarPlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Dra> dras,
                            float toPlanetRotSpd) {
        myPlanet = planet;
        myRelAngleToPlanet = relAngleToPlanet;
        myDist = dist;
        myDras = dras;
        myRadius = DraMan.radiusFromDras(myDras);
        myToPlanetRotSpd = toPlanetRotSpd;
        myPos = new Vector2();
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
        return false;
    }

    @Override
    public SolObject toObj(SolGame game) {
        return new PlanetSprites(myPlanet, myRelAngleToPlanet, myDist, myDras, myToPlanetRotSpd);
    }

    @Override
    public void update(SolGame game) {
        myRelAngleToPlanet += myToPlanetRotSpd * game.getTimeStep();
        if (game.getPlanetMan().getNearestPlanet() == myPlanet) {
            SolMath.fromAl(myPos, myPlanet.getAngle() + myRelAngleToPlanet, myDist);
            myPos.add(myPlanet.getPos());
        }
    }

    @Override
    public float getRadius() {
        return myRadius;
    }

    @Override
    public Vector2 getPos() {
        return myPos;
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
