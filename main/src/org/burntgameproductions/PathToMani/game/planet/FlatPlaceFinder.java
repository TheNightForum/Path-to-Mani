

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class FlatPlaceFinder {
  private final Vector2 myVec = new Vector2();
  private float myDeviation;

  private final RayCastCallback myRayBack = new RayCastCallback() {
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      if (!(fixture.getBody().getUserData() instanceof TileObject)) {
        return -1;
      }
      myVec.set(point);
      myDeviation = ManiMath.abs(ManiMath.angle(normal) + 90);
      return fraction;
    }
  };

  public Vector2 find(ManiGame game, Planet p, ConsumedAngles takenAngles, float objHalfWidth) {
    Vector2 pPos = p.getPos();

    Vector2 res = new Vector2(pPos);
    float minDeviation = 90;
    float resAngle = 0;
    float objAngularHalfWidth = ManiMath.angularWidthOfSphere(objHalfWidth, p.getGroundHeight());

    for (int i = 0; i < 20; i++) {
      float angle = ManiMath.rnd(180);
      if (takenAngles != null && takenAngles.isConsumed(angle, objAngularHalfWidth)) continue;
      myDeviation = angle;
      ManiMath.fromAl(myVec, angle, p.getFullHeight());
      myVec.add(pPos);
      game.getObjMan().getWorld().rayCast(myRayBack, myVec, pPos);
      if (myDeviation < minDeviation) {
        res.set(myVec);
        minDeviation = myDeviation;
        resAngle = angle;
      }
    }

    if (takenAngles != null) takenAngles.add(resAngle, objAngularHalfWidth);
    res.sub(pPos);
    ManiMath.rotate(res, -p.getAngle());
    return res;
  }
}
