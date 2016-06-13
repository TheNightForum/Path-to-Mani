

package org.burntgameproductions.PathToMani.game.input;

import org.burntgameproductions.PathToMani.game.Faction;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.FarShip;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public interface Pilot {
  void update(ManiGame game, ManiShip ship, ManiShip nearestEnemy);
  boolean isUp();
  boolean isLeft();
  boolean isRight();
  boolean isShoot();
  boolean isShoot2();
  boolean collectsItems();
  boolean isAbility();
  Faction getFaction();
  boolean shootsAtObstacles();
  float getDetectionDist();
  String getMapHint();
  void updateFar(ManiGame game, FarShip farShip);
  String toDebugString();
  boolean isPlayer();

  public static final class Utils {
    public static boolean isIdle(Pilot p) {
      return !(p.isUp() || p.isShoot() || p.isShoot2() || p.isAbility());
    }
  }
}
