

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

public interface MoveDestProvider {
  Vector2 getDest();
  boolean shouldAvoidBigObjs();

  /**
   * @return the desired spd lenght both for peaceful movement and for maneuvering
   */
  float getDesiredSpdLen();
  boolean shouldStopNearDest();
  void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy);

  /**
   * if true is returned, the ship will move in battle pattern around the enemy and try to face enemy with guns
   * if false is returned, the ship will try to avoid projectiles or fly away from enemy (not implemented yet!)
   * if null is returned, the ship will move as if there's no enemy near
   * note that the ship will always shoot if there's enemy ahead of it (or if it has unfixed gun)
   */
  Boolean shouldManeuver(boolean canShoot, ManiShip nearestEnemy, boolean nearGround);

  Vector2 getDestSpd();
}
