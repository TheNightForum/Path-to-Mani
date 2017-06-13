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
package old.tnf.ptm.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.tnf.ptm.common.CollisionMeshLoader;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.entities.asteroid.AsteroidBuilder;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.handler.dra.DraLevel;
import com.tnf.ptm.entities.ship.ShipBuilder;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;

public class ShardBuilder {
    public static final float MIN_SCALE = .07f;
    public static final float MAX_SCALE = .12f;
    public static final float SIZE_TO_SHARD_COUNT = 13f;
    private static final float MAX_ROT_SPD = 5f;
    private static final float MAX_SPD = 4f;
    private final CollisionMeshLoader myCollisionMeshLoader;
    private final ArrayList<TextureAtlas.AtlasRegion> myTexs;

    public ShardBuilder(TextureManager textureManager) {
        myCollisionMeshLoader = new CollisionMeshLoader(new ResourceUrn("core:misc"));
        myTexs = textureManager.getPack("smallGameObjects/shard");
    }

    public void buildExplosionShards(PtmGame game, Vector2 pos, Vector2 baseSpd, float size) {
        int count = (int) (size * SIZE_TO_SHARD_COUNT);
        for (int i = 0; i < count; i++) {
            Shard s = build(game, pos, baseSpd, size);
            game.getObjMan().addObjDelayed(s);
        }
    }

    public Shard build(PtmGame game, Vector2 basePos, Vector2 baseSpd, float size) {

        ArrayList<Dra> dras = new ArrayList<Dra>();
        float scale = PtmMath.rnd(MIN_SCALE, MAX_SCALE);
        TextureAtlas.AtlasRegion tex = PtmMath.elemRnd(myTexs);
        float spdAngle = PtmMath.rnd(180);
        Vector2 pos = new Vector2();
        PtmMath.fromAl(pos, spdAngle, PtmMath.rnd(size));
        pos.add(basePos);
        Body body = myCollisionMeshLoader.getBodyAndSprite(game, "smallGameObjects", AsteroidBuilder.removePath(tex.name) + "_" + tex.index, scale,
                BodyDef.BodyType.DynamicBody, pos, PtmMath.rnd(180), dras, ShipBuilder.SHIP_DENSITY, DraLevel.PROJECTILES, tex);

        body.setAngularVelocity(PtmMath.rnd(MAX_ROT_SPD));
        Vector2 spd = PtmMath.fromAl(spdAngle, PtmMath.rnd(MAX_SPD));
        spd.add(baseSpd);
        body.setLinearVelocity(spd);
        PtmMath.free(spd);

        Shard shard = new Shard(body, dras);
        body.setUserData(shard);
        return shard;
    }
}
