

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class PlanetBind {
  private final Planet myPlanet;
  private final Vector2 myRelPos;
  private final float myRelAngle;

  public PlanetBind(Planet planet, Vector2 pos, float angle) {
    myPlanet = planet;
    myRelPos = new Vector2();
    float planetAngle = planet.getAngle();
    ManiMath.toRel(pos, myRelPos, planetAngle, planet.getPos());
    myRelAngle = angle - planetAngle;
  }

  public void setDiff(Vector2 diff, Vector2 pos, boolean precise) {
    ManiMath.toWorld(diff, myRelPos, myPlanet.getAngle(), myPlanet.getPos(), precise);
    diff.sub(pos);
  }

  public float getDesiredAngle() {
    return myPlanet.getAngle() + myRelAngle;
  }

  public static PlanetBind tryBind(ManiGame game, Vector2 pos, float angle) {
    Planet np = game.getPlanetMan().getNearestPlanet(pos);
    if (!np.isNearGround(pos)) return null;
    return new PlanetBind(np, pos, angle);
  }

  public Planet getPlanet() {
    return myPlanet;
  }
}
