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

package com.tnf.ptm.entities.gun;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.common.PtmObject;
import com.tnf.ptm.handler.dra.RectSprite;
import com.tnf.ptm.entities.item.Clip;
import com.tnf.ptm.entities.planet.Planet;
import com.tnf.ptm.entities.projectile.ProjectileConfig;
import com.tnf.ptm.entities.Faction;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.handler.dra.DraLevel;
import com.tnf.ptm.entities.item.Gun;
import com.tnf.ptm.entities.item.ItemContainer;
import com.tnf.ptm.gfx.particle.LightSrc;
import com.tnf.ptm.entities.projectile.Projectile;

import java.util.ArrayList;
import java.util.List;

public class PtmGun {
    private final LightSrc myLightSrc;
    private final Vector2 myRelPos;
    private final RectSprite mySprite;
    private final Gun myItem;
    private final List<Dra> myDras;
    private float myCoolDown;
    private float myCurrAngleVar;

    public PtmGun(PtmGame game, Gun item, Vector2 relPos, boolean underShip) {
        myItem = item;
        if (myItem.config.lightOnShot) {
            Color lightCol = PtmColor.WHITE;
            ProjectileConfig projConfig = myItem.config.clipConf.projConfig;
            if (projConfig.bodyEffect != null) {
                lightCol = projConfig.bodyEffect.tint;
            } else if (projConfig.collisionEffect != null) {
                lightCol = projConfig.collisionEffect.tint;
            }
            myLightSrc = new LightSrc(game, .25f, true, 1f, Vector2.Zero, lightCol);
        } else {
            myLightSrc = null;
        }
        myRelPos = new Vector2(relPos);
        DraLevel level = underShip ? DraLevel.U_GUNS : DraLevel.GUNS;
        float texLen = myItem.config.gunLength / myItem.config.texLenPerc * 2;
        mySprite = new RectSprite(myItem.config.tex, texLen, 0, 0, new Vector2(relPos), level, 0, 0, PtmColor.WHITE, false);
        myDras = new ArrayList<Dra>();
        myDras.add(mySprite);
        if (myLightSrc != null) {
            myLightSrc.collectDras(myDras);
        }
    }

    public List<Dra> getDras() {
        return myDras;
    }

    private void shoot(Vector2 gunSpd, PtmGame game, float gunAngle, Vector2 muzzlePos, Faction faction, PtmObject creator) {
        Vector2 baseSpd = gunSpd;
        Clip.Config cc = myItem.config.clipConf;
        if (cc.projConfig.zeroAbsSpd) {
            baseSpd = Vector2.Zero;
            Planet np = game.getPlanetMan().getNearestPlanet();
            if (np.isNearGround(muzzlePos)) {
                baseSpd = new Vector2();
                np.calcSpdAtPos(baseSpd, muzzlePos);
            }
        }

        myCurrAngleVar = PtmMath.approach(myCurrAngleVar, myItem.config.maxAngleVar, myItem.config.angleVarPerShot);
        boolean multiple = cc.projectilesPerShot > 1;
        for (int i = 0; i < cc.projectilesPerShot; i++) {
            float bulletAngle = gunAngle;
            if (myCurrAngleVar > 0) {
                bulletAngle += PtmMath.rnd(myCurrAngleVar);
            }
            Projectile proj = new Projectile(game, bulletAngle, muzzlePos, baseSpd, faction, cc.projConfig, multiple);
            game.getObjMan().addObjDelayed(proj);
        }
        myCoolDown += myItem.config.timeBetweenShots;
        myItem.ammo--;
        game.getSoundManager().play(game, myItem.config.shootSound, muzzlePos, creator);
    }

    public void update(ItemContainer ic, PtmGame game, float gunAngle, PtmObject creator, boolean shouldShoot, Faction faction) {
        float baseAngle = creator.getAngle();
        Vector2 basePos = creator.getPosition();
        float gunRelAngle = gunAngle - baseAngle;
        mySprite.relAngle = gunRelAngle;
        Vector2 muzzleRelPos = PtmMath.fromAl(gunRelAngle, myItem.config.gunLength);
        muzzleRelPos.add(myRelPos);
        if (myLightSrc != null) {
            myLightSrc.setRelPos(muzzleRelPos);
        }
        Vector2 muzzlePos = PtmMath.toWorld(muzzleRelPos, baseAngle, basePos);
        PtmMath.free(muzzleRelPos);

        float ts = game.getTimeStep();
        if (myItem.ammo <= 0 && myItem.reloadAwait <= 0) {
            if (myItem.config.clipConf.infinite || ic != null && ic.tryConsumeItem(myItem.config.clipConf.example)) {
                myItem.reloadAwait = myItem.config.reloadTime + .0001f;
                game.getSoundManager().play(game, myItem.config.reloadSound, null, creator);
            }
        } else if (myItem.reloadAwait > 0) {
            myItem.reloadAwait -= ts;
            if (myItem.reloadAwait <= 0) {
                myItem.ammo = myItem.config.clipConf.size;
            }
        }

        if (myCoolDown > 0) {
            myCoolDown -= ts;
        }

        boolean shot = shouldShoot && myCoolDown <= 0 && myItem.ammo > 0;
        if (shot) {
            Vector2 gunSpd = creator.getSpd();
            shoot(gunSpd, game, gunAngle, muzzlePos, faction, creator);
        } else {
            myCurrAngleVar = PtmMath.approach(myCurrAngleVar, myItem.config.minAngleVar, myItem.config.angleVarDamp * ts);
        }
        if (myLightSrc != null) {
            myLightSrc.update(shot, baseAngle, game);
        }
        PtmMath.free(muzzlePos);
    }

    public Gun.Config getConfig() {
        return myItem.config;
    }

    public Gun getItem() {
        return myItem;
    }
}
