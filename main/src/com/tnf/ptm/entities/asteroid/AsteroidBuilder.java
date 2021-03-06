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
package com.tnf.ptm.entities.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.common.RemoveController;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.handler.dra.DraLevel;
import com.tnf.ptm.handler.dra.RectSprite;
import com.tnf.ptm.common.CollisionMeshLoader;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;

public class AsteroidBuilder {
    public static final float DENSITY = 10f;
    private static final float MAX_A_ROT_SPD = .5f;
    private static final float MAX_BALL_SZ = .2f;
    private final CollisionMeshLoader myCollisionMeshLoader;
    private final ArrayList<TextureAtlas.AtlasRegion> myTexs;

    public AsteroidBuilder(TextureManager textureManager) {
        myCollisionMeshLoader = new CollisionMeshLoader(new ResourceUrn("core:asteroids"));
        myTexs = textureManager.getPack("asteroids/sys");
    }

    public static String removePath(String name) {
        String[] parts = name.split("[/\\\\]");
        return parts[parts.length - 1];
    }

    public static Body buildBall(PtmGame game, Vector2 pos, float angle, float rad, float density, boolean sensor) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.angle = angle * PtmMath.degRad;
        bd.angularDamping = 0;
        bd.position.set(pos);
        bd.linearDamping = 0;
        Body body = game.getObjMan().getWorld().createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.density = density;
        fd.friction = Const.FRICTION;
        fd.shape = new CircleShape();
        fd.shape.setRadius(rad);
        fd.isSensor = sensor;
        body.createFixture(fd);
        fd.shape.dispose();
        return body;
    }

    // doesn't consume pos
    public Asteroid buildNew(PtmGame game, Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
        float rotSpd = PtmMath.rnd(MAX_A_ROT_SPD);
        return build(game, pos, PtmMath.elemRnd(myTexs), sz, PtmMath.rnd(180), rotSpd, spd, removeController);
    }

    // doesn't consume pos
    public FarAsteroid buildNewFar(Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
        float rotSpd = PtmMath.rnd(MAX_A_ROT_SPD);
        return new FarAsteroid(PtmMath.elemRnd(myTexs), new Vector2(pos), PtmMath.rnd(180), removeController, sz, new Vector2(spd), rotSpd);
    }

    // doesn't consume pos
    public Asteroid build(PtmGame game, Vector2 pos, TextureAtlas.AtlasRegion tex, float sz, float angle, float rotSpd, Vector2 spd, RemoveController removeController) {

        ArrayList<Dra> dras = new ArrayList<Dra>();
        Body body;
        if (MAX_BALL_SZ < sz) {
            body = myCollisionMeshLoader.getBodyAndSprite(game, "asteroids", removePath(tex.name) + "_" + tex.index, sz,
                    BodyDef.BodyType.DynamicBody, pos, angle, dras, DENSITY, DraLevel.BODIES, tex);
        } else {
            body = buildBall(game, pos, angle, sz / 2, DENSITY, false);
            RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.BODIES, 0, 0, PtmColor.WHITE, false);
            dras.add(s);
        }
        body.setAngularVelocity(rotSpd);
        body.setLinearVelocity(spd);

        Asteroid res = new Asteroid(game, tex, body, sz, removeController, dras);
        body.setUserData(res);
        return res;
    }
}
