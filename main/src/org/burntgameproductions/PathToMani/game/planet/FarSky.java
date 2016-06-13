

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ManiObject;

public class FarSky implements FarObj {
  private final Planet myPlanet;

  public FarSky(Planet planet) {
    myPlanet = planet;
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return false;
  }

  @Override
  public ManiObject toObj(ManiGame game) {
    return new Sky(game, myPlanet);
  }

  @Override
  public void update(ManiGame game) {
  }

  @Override
  public float getRadius() {
    return myPlanet.getGroundHeight() + Const.MAX_SKY_HEIGHT_FROM_GROUND;
  }

  @Override
  public Vector2 getPos() {
    return myPlanet.getPos();
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public boolean hasBody() {
    return false;
  }
}
