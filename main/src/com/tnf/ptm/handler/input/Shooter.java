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
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.entities.gun.GunMount;
import com.tnf.ptm.entities.item.Gun;
import com.tnf.ptm.entities.projectile.ProjectileConfig;
import com.tnf.ptm.entities.ship.PtmShip;

public class Shooter {

    public static final float E_SPD_PERC = .6f; // 0 means that target speed is not considered, 1 means that it's fully considered
    public static final float MIN_SHOOT_AAD = 2f;
    private boolean myShoot;
    private boolean myShoot2;
    private boolean myRight;
    private boolean myLeft;

    public Shooter() {
    }

    public static float calcShootAngle(Vector2 gunPos, Vector2 gunSpd, Vector2 ePos, Vector2 eSpd, float projSpd,
                                       boolean sharp) {
        Vector2 eSpdShortened = PtmMath.getVec(eSpd);
        if (!sharp) {
            eSpdShortened.scl(E_SPD_PERC);
        }
        Vector2 relESpd = PtmMath.distVec(gunSpd, eSpdShortened);
        PtmMath.free(eSpdShortened);
        float rotAngle = PtmMath.angle(relESpd);
        float v = relESpd.len();
        float v2 = projSpd;
        PtmMath.free(relESpd);
        Vector2 toE = PtmMath.distVec(gunPos, ePos);
        PtmMath.rotate(toE, -rotAngle);
        float x = toE.x;
        float y = toE.y;
        float a = v * v - v2 * v2;
        float b = 2 * x * v;
        float c = x * x + y * y;
        float t = PtmMath.genQuad(a, b, c);
        float res;
        if (t != t) {
            res = Float.NaN;
        } else {
            toE.x += t * v;
            res = PtmMath.angle(toE) + rotAngle;
        }
        PtmMath.free(toE);
        return res;
    }

    public void update(PtmShip ship, Vector2 enemyPos, boolean dontRotate, boolean canShoot, Vector2 enemySpd,
                       float enemyApproxRad) {
        myLeft = false;
        myRight = false;
        myShoot = false;
        myShoot2 = false;
        Vector2 shipPos = ship.getPosition();
        if (enemyPos == null || !canShoot) {
            return;
        }
        float toEnemyDst = enemyPos.dst(shipPos);

        Gun g1 = processGun(ship, false);
        Gun g2 = processGun(ship, true);
        if (g1 == null && g2 == null) {
            return;
        }

        float projSpd = 0;
        Gun g = null;
        if (g1 != null) {
            ProjectileConfig projConfig = g1.config.clipConf.projConfig;
            projSpd = projConfig.spdLen + projConfig.acc; // for simplicity
            g = g1;
        }
        if (g2 != null) {
            ProjectileConfig projConfig = g2.config.clipConf.projConfig;
            float g2PS = projConfig.spdLen + projConfig.acc; // for simplicity
            if (projSpd < g2PS) {
                projSpd = g2PS;
                g = g2;
            }
        }

        Vector2 gunRelPos = ship.getHull().getGunMount(g == g2).getRelPos();
        Vector2 gunPos = PtmMath.toWorld(gunRelPos, ship.getAngle(), shipPos);
        float shootAngle = calcShootAngle(gunPos, ship.getSpd(), enemyPos, enemySpd, projSpd, false);
        PtmMath.free(gunPos);
        if (shootAngle != shootAngle) {
            return;
        }
        {
            // ok this is a hack
            float toShip = PtmMath.angle(enemyPos, shipPos);
            float toGun = PtmMath.angle(enemyPos, gunPos);
            shootAngle += toGun - toShip;
        }
        float shipAngle = ship.getAngle();
        float maxAngleDiff = PtmMath.angularWidthOfSphere(enemyApproxRad, toEnemyDst) + 10f;
        ProjectileConfig projConfig = g.config.clipConf.projConfig;
        if (projSpd > 0 && projConfig.guideRotSpd > 0) {
            maxAngleDiff += projConfig.guideRotSpd * toEnemyDst / projSpd;
        }
        if (PtmMath.angleDiff(shootAngle, shipAngle) < maxAngleDiff) {
            myShoot = true;
            myShoot2 = true;
            return;
        }

        if (dontRotate) {
            return;
        }
        Boolean ntt = Mover.needsToTurn(shipAngle, shootAngle, ship.getRotSpd(), ship.getRotAcc(), MIN_SHOOT_AAD);
        if (ntt != null) {
            if (ntt) {
                myRight = true;
            } else {
                myLeft = true;
            }
        }
    }

    // returns gun if it's fixed & can shoot
    private Gun processGun(PtmShip ship, boolean second) {
        GunMount mount = ship.getHull().getGunMount(second);
        if (mount == null) {
            return null;
        }
        Gun g = mount.getGun();
        if (g == null || g.ammo <= 0) {
            return null;
        }

        if (g.config.clipConf.projConfig.zeroAbsSpd || g.config.clipConf.projConfig.guideRotSpd > 0) {
            if (second) {
                myShoot2 = true;
            } else {
                myShoot = true;
            }
            return null;
        }

        if (g.config.fixed) {
            return g;
        }

        if (mount.isDetected()) {
            if (second) {
                myShoot2 = true;
            } else {
                myShoot = true;
            }
        }
        return null;
    }

    public boolean isShoot() {
        return myShoot;
    }

    public boolean isShoot2() {
        return myShoot2;
    }

    public boolean isLeft() {
        return myLeft;
    }

    public boolean isRight() {
        return myRight;
    }
}
