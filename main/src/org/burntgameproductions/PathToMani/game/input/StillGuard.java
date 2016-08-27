

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ShipConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.planet.PlanetBind;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

public class StillGuard implements MoveDestProvider {

  private final PlanetBind myPlanetBind;
  private final float myDesiredSpdLen;
  private Vector2 myDest;
  private Vector2 myDestSpd;

  public StillGuard(Vector2 target, ManiGame game, ShipConfig sc) {
    myDest = new Vector2(target);
    myPlanetBind = PlanetBind.tryBind(game, myDest, 0);
    myDesiredSpdLen = sc.hull.getType() == HullConfig.Type.BIG ? Const.BIG_AI_SPD : Const.DEFAULT_AI_SPD;
    myDestSpd = new Vector2();
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return myPlanetBind != null;
  }

  @Override
  public float getDesiredSpdLen() {
    return myDesiredSpdLen;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy) {
    if (myPlanetBind != null) {
      Vector2 diff = ManiMath.getVec();
      myPlanetBind.setDiff(diff, myDest, false);
      myDest.add(diff);
      ManiMath.free(diff);
      myPlanetBind.getPlanet().calcSpdAtPos(myDestSpd, myDest);
    }
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, ManiShip nearestEnemy, boolean nearGround) {
    return true;
  }

  @Override
  public Vector2 getDestSpd() {
    return myDestSpd;
  }
}
