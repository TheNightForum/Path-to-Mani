

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

/**
 * Just stays wherever it is, but maneuvers
 */
public class NoDestProvider implements MoveDestProvider {

  public NoDestProvider() {
  }

  @Override
  public Vector2 getDest() {
    return null;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return Const.DEFAULT_AI_SPD;
  }

  @Override
  public boolean shouldStopNearDest() {
    return false;
  }

  @Override
  public void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy) {
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
