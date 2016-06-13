

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.dra.DraMan;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.ManiGame;

import java.util.List;

public class FarPlanetSprites implements FarObj {
  private final Planet myPlanet;
  private float myRelAngleToPlanet;
  private final float myDist;
  private final List<Dra> myDras;
  private final float myRadius;
  private final float myToPlanetRotSpd;
  private Vector2 myPos;

  public FarPlanetSprites(Planet planet, float relAngleToPlanet, float dist, List<Dra> dras,
    float toPlanetRotSpd) {
    myPlanet = planet;
    myRelAngleToPlanet = relAngleToPlanet;
    myDist = dist;
    myDras = dras;
    myRadius = DraMan.radiusFromDras(myDras);
    myToPlanetRotSpd = toPlanetRotSpd;
    myPos = new Vector2();
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return false;
  }

  @Override
  public ManiObject toObj(ManiGame game) {
    return new PlanetSprites(myPlanet, myRelAngleToPlanet, myDist, myDras, myToPlanetRotSpd);
  }

  @Override
  public void update(ManiGame game) {
    myRelAngleToPlanet += myToPlanetRotSpd * game.getTimeStep();
    if (game.getPlanetMan().getNearestPlanet() == myPlanet) {
      SolMath.fromAl(myPos, myPlanet.getAngle() + myRelAngleToPlanet, myDist);
      myPos.add(myPlanet.getPos());
    }
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public Vector2 getPos() {
    return myPos;
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
