/*
 * Copyright 2016 BurntGameProductions
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

package com.pathtomani.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.dra.Dra;
import com.pathtomani.entities.ship.ShipBuilder;
import com.pathtomani.common.TextureManager;
import com.pathtomani.entities.asteroid.AsteroidBuilder;
import com.pathtomani.game.dra.DraLevel;

import java.util.ArrayList;

public class ShardBuilder {
  private static final float MAX_ROT_SPD = 5f;
  private static final float MAX_SPD = 4f;
  public static final float MIN_SCALE = .07f;
  public static final float MAX_SCALE = .12f;
  public static final float SIZE_TO_SHARD_COUNT = 13f;

  private final PathLoader myPathLoader;
  private final ArrayList<TextureAtlas.AtlasRegion> myTexs;

  public ShardBuilder(TextureManager textureManager) {
    myPathLoader = new PathLoader("misc");
    myTexs = textureManager.getPack("smallGameObjs/shard", null);
  }

  public void buildExplosionShards(ManiGame game, Vector2 pos, Vector2 baseSpd, float size) {
    int count = (int) (size * SIZE_TO_SHARD_COUNT);
    for (int i = 0; i < count; i++) {
      Shard s = build(game, pos, baseSpd, size);
      game.getObjMan().addObjDelayed(s);
    }
  }

  public Shard build(ManiGame game, Vector2 basePos, Vector2 baseSpd, float size) {

    ArrayList<Dra> dras = new ArrayList<Dra>();
    float scale = ManiMath.rnd(MIN_SCALE, MAX_SCALE);
    TextureAtlas.AtlasRegion tex = ManiMath.elemRnd(myTexs);
    float spdAngle = ManiMath.rnd(180);
    Vector2 pos = new Vector2();
    ManiMath.fromAl(pos, spdAngle, ManiMath.rnd(size));
    pos.add(basePos);
    Body body = myPathLoader.getBodyAndSprite(game, "smallGameObjs", AsteroidBuilder.removePath(tex.name) + "_" + tex.index, scale,
      BodyDef.BodyType.DynamicBody, pos, ManiMath.rnd(180), dras, ShipBuilder.SHIP_DENSITY, DraLevel.PROJECTILES, tex);

    body.setAngularVelocity(ManiMath.rnd(MAX_ROT_SPD));
    Vector2 spd = ManiMath.fromAl(spdAngle, ManiMath.rnd(MAX_SPD));
    spd.add(baseSpd);
    body.setLinearVelocity(spd);
    ManiMath.free(spd);

    Shard shard = new Shard(body, dras);
    body.setUserData(shard);
    return shard;
  }
}
