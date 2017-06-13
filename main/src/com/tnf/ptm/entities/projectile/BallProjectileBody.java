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
package com.tnf.ptm.entities.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.entities.asteroid.AsteroidBuilder;
import com.tnf.ptm.entities.ship.PtmShip;

public class BallProjectileBody implements ProjectileBody {
    private final Body myBody;
    private final Vector2 myPos;
    private final Vector2 mySpd;
    private final float myAcc;
    private final float myMass;

    private float myAngle;

    public BallProjectileBody(PtmGame game, Vector2 pos, float angle, Projectile projectile,
                              Vector2 gunSpd, float spdLen, ProjectileConfig config) {
        float density = config.density == -1 ? 1 : config.density;
        myBody = AsteroidBuilder.buildBall(game, pos, angle, config.physSize / 2, density, config.massless);
        if (config.zeroAbsSpd) {
            myBody.setAngularVelocity(15f * PtmMath.degRad);
        }

        mySpd = new Vector2();
        PtmMath.fromAl(mySpd, angle, spdLen);
        mySpd.add(gunSpd);
        myBody.setLinearVelocity(mySpd);
        myBody.setUserData(projectile);

        myPos = new Vector2();
        myAcc = config.acc;
        myMass = myBody.getMass();
        setParamsFromBody();
    }

    private void setParamsFromBody() {
        myPos.set(myBody.getPosition());
        myAngle = myBody.getAngle() * PtmMath.radDeg;
        mySpd.set(myBody.getLinearVelocity());
    }

    @Override
    public void update(PtmGame game) {
        setParamsFromBody();
        if (myAcc > 0 && PtmMath.canAccelerate(myAngle, mySpd)) {
            Vector2 force = PtmMath.fromAl(myAngle, myAcc * myMass);
            myBody.applyForceToCenter(force, true);
            PtmMath.free(force);
        }
    }

    @Override
    public Vector2 getPos() {
        return myPos;
    }

    @Override
    public Vector2 getSpd() {
        return mySpd;
    }

    @Override
    public void receiveForce(Vector2 force, PtmGame game, boolean acc) {
        if (acc) {
            force.scl(myMass);
        }
        myBody.applyForceToCenter(force, true);
    }

    @Override
    public void onRemove(PtmGame game) {
        myBody.getWorld().destroyBody(myBody);
    }

    @Override
    public float getAngle() {
        return myAngle;
    }

    @Override
    public void changeAngle(float diff) {
        myAngle += diff;
        myBody.setTransform(myPos, myAngle * PtmMath.degRad);
        myBody.setAngularVelocity(0);
    }

    @Override
    public float getDesiredAngle(PtmShip ne) {
        float spdLen = mySpd.len();
        if (spdLen < 3) {
            spdLen = 3;
        }
        float toNe = PtmMath.angle(myPos, ne.getPosition());
        Vector2 desiredSpd = PtmMath.fromAl(toNe, spdLen);
        desiredSpd.add(ne.getSpd());
        float res = PtmMath.angle(mySpd, desiredSpd);
        PtmMath.free(desiredSpd);
        return res;
    }
}
