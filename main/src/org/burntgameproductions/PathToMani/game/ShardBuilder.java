

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.asteroid.AsteroidBuilder;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.dra.DraLevel;
import org.burntgameproductions.PathToMani.game.ship.ShipBuilder;

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
