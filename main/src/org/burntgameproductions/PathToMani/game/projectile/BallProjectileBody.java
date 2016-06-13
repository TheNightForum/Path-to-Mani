

package org.burntgameproductions.PathToMani.game.projectile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.asteroid.AsteroidBuilder;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public class BallProjectileBody implements ProjectileBody {
  private final Body myBody;
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final float myAcc;
  private final float myMass;

  private float myAngle;

  public BallProjectileBody(ManiGame game, Vector2 pos, float angle, Projectile projectile,
                            Vector2 gunSpd, float spdLen, ProjectileConfig config)
  {
    float density = config.density == -1 ? 1 : config.density;
    myBody = AsteroidBuilder.buildBall(game, pos, angle, config.physSize / 2, density, config.massless);
    if (config.zeroAbsSpd) myBody.setAngularVelocity(15f * SolMath.degRad);

    mySpd = new Vector2();
    SolMath.fromAl(mySpd, angle, spdLen);
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
    myAngle = myBody.getAngle() * SolMath.radDeg;
    mySpd.set(myBody.getLinearVelocity());
  }

  @Override
  public void update(ManiGame game) {
    setParamsFromBody();
    if (myAcc > 0 && SolMath.canAccelerate(myAngle, mySpd)) {
      Vector2 force = SolMath.fromAl(myAngle, myAcc * myMass);
      myBody.applyForceToCenter(force, true);
      SolMath.free(force);
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
  public void receiveForce(Vector2 force, ManiGame game, boolean acc) {
    if (acc) force.scl(myMass);
    myBody.applyForceToCenter(force, true);
  }

  @Override
  public void onRemove(ManiGame game) {
    myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public float getAngle() {
    return myAngle;
  }

  @Override
  public void changeAngle(float diff) {
    myAngle += diff;
    myBody.setTransform(myPos, myAngle * SolMath.degRad);
    myBody.setAngularVelocity(0);
  }

  @Override
  public float getDesiredAngle(ManiShip ne) {
    float spdLen = mySpd.len();
    if (spdLen < 3) spdLen = 3;
    float toNe = SolMath.angle(myPos, ne.getPosition());
    Vector2 desiredSpd = SolMath.fromAl(toNe, spdLen);
    desiredSpd.add(ne.getSpd());
    float res = SolMath.angle(mySpd, desiredSpd);
    SolMath.free(desiredSpd);
    return res;
  }
}
