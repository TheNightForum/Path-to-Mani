

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

/**
 * Flies in the planet orbit
 */
public class OrbiterDestProvider implements MoveDestProvider {
  private final Planet myPlanet;
  private final float myDesiredSpd;
  private final float myHeight;
  private final boolean myCw;
  private final Vector2 myDest;

  public OrbiterDestProvider(Planet planet, float height, boolean cw) {
    myPlanet = planet;
    myHeight = height;
    myCw = cw;
    myDesiredSpd = SolMath.sqrt(myPlanet.getGravConst() / myHeight);
    myDest = new Vector2();
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return myDesiredSpd;
  }

  @Override
  public boolean shouldStopNearDest() {
    return false;
  }

  @Override
  public void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy) {
    Vector2 pPos = myPlanet.getPos();
    float destAngle = SolMath.angle(pPos, shipPos) + 5 * SolMath.toInt(myCw);
    SolMath.fromAl(myDest, destAngle, myHeight);
    myDest.add(pPos);
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, ManiShip nearestEnemy, boolean nearGround) {
    return null;
  }

  @Override
  public Vector2 getDestSpd() {
    return Vector2.Zero;
  }
}
