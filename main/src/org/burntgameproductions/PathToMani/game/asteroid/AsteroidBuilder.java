

package org.burntgameproductions.PathToMani.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.dra.DraLevel;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.game.PathLoader;
import org.burntgameproductions.PathToMani.game.RemoveController;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.dra.RectSprite;

import java.util.ArrayList;

public class AsteroidBuilder {
  private static final float MAX_A_ROT_SPD = .5f;
  private static final float MAX_BALL_SZ = .2f;
  public static final float DENSITY = 10f;

  private final PathLoader myPathLoader;
  private final ArrayList<TextureAtlas.AtlasRegion> myTexs;

  public AsteroidBuilder(TextureManager textureManager) {
    myPathLoader = new PathLoader("asteroids");
    myTexs = textureManager.getPack("asteroids/sys", null);
  }

  // doesn't consume pos
  public Asteroid buildNew(ManiGame game, Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
    float rotSpd = ManiMath.rnd(MAX_A_ROT_SPD);
    return build(game, pos, ManiMath.elemRnd(myTexs), sz, ManiMath.rnd(180), rotSpd, spd, removeController);
  }

  // doesn't consume pos
  public FarAsteroid buildNewFar(Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
    float rotSpd = ManiMath.rnd(MAX_A_ROT_SPD);
    return new FarAsteroid(ManiMath.elemRnd(myTexs), new Vector2(pos), ManiMath.rnd(180), removeController, sz, new Vector2(spd), rotSpd);
  }

  // doesn't consume pos
  public Asteroid build(ManiGame game, Vector2 pos, TextureAtlas.AtlasRegion tex, float sz, float angle, float rotSpd, Vector2 spd, RemoveController removeController) {

    ArrayList<Dra> dras = new ArrayList<Dra>();
    Body body;
    if (MAX_BALL_SZ < sz) {
      body = myPathLoader.getBodyAndSprite(game, "asteroids", removePath(tex.name) + "_" + tex.index, sz,
        BodyDef.BodyType.DynamicBody, pos, angle, dras, DENSITY, DraLevel.BODIES, tex);
    } else {
      body = buildBall(game, pos, angle, sz/2, DENSITY, false);
      RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.BODIES, 0, 0, ManiColor.W, false);
      dras.add(s);
    }
    body.setAngularVelocity(rotSpd);
    body.setLinearVelocity(spd);

    Asteroid res = new Asteroid(game, tex, body, sz, removeController, dras);
    body.setUserData(res);
    return res;
  }

  public static String removePath(String name) {
    String[] parts = name.split("[/\\\\]");
    return parts[parts.length - 1];
  }

  public static Body buildBall(ManiGame game, Vector2 pos, float angle, float rad, float density, boolean sensor) {
    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = angle * ManiMath.degRad;
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
}
