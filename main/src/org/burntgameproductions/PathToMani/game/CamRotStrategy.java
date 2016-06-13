

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.planet.ManiSystem;

public interface CamRotStrategy {
  public float getRotation(Vector2 pos, ManiGame game);

  public static class Static implements CamRotStrategy {
    public float getRotation(Vector2 pos, ManiGame game) {
      return 0;
    }
  }

  public static class ToPlanet implements CamRotStrategy {

    public float getRotation(Vector2 pos, ManiGame game) {
      Planet np = game.getPlanetMan().getNearestPlanet();
      float fh = np.getFullHeight();
      Vector2 npPos = np.getPos();
      if (npPos.dst(pos) < fh) {
        return ManiMath.angle(pos, npPos, true) - 90;
      }
      ManiSystem sys = game.getPlanetMan().getNearestSystem(pos);
      Vector2 sysPos = sys.getPos();
      if (sysPos.dst(pos) < Const.SUN_RADIUS) {
        return ManiMath.angle(pos, sysPos, true) - 90;
      }
      return 0;
    }
  }
}
