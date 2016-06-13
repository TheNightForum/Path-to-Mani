

package org.burntgameproductions.PathToMani.game.projectile;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public interface ProjectileBody {
  void update(ManiGame game);
  Vector2 getPos();
  Vector2 getSpd();
  void receiveForce(Vector2 force, ManiGame game, boolean acc);
  void onRemove(ManiGame game);
  float getAngle();
  void changeAngle(float diff);
  float getDesiredAngle(ManiShip ne);
}
