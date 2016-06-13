

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public class CollisionWarnDrawer extends WarnDrawer {
  private final MyRayBack myWarnCallback = new MyRayBack();
  private ManiShip myHero;

  public CollisionWarnDrawer(float r) {
    super(r, "Object Near");
  }

  public boolean shouldWarn(ManiGame game) {
    myHero = game.getHero();
    if (myHero == null) return false;
    Vector2 pos = myHero.getPosition();
    Vector2 spd = myHero.getSpd();
    float acc = myHero.getAcc();
    float spdLen = spd.len();
    float spdAngle = ManiMath.angle(spd);
    if (acc <= 0 || spdLen < 2 * acc) return false;
    // t = v/a;
    // s = att/2 = vv/a/2;
    float breakWay = spdLen * spdLen / acc / 2;
    breakWay += 2 * spdLen;
    Vector2 finalPos = ManiMath.getVec(0, 0);
    ManiMath.fromAl(finalPos, spdAngle, breakWay);
    finalPos.add(pos);
    myWarnCallback.show = false;
    game.getObjMan().getWorld().rayCast(myWarnCallback, pos, finalPos);
    ManiMath.free(finalPos);
    return myWarnCallback.show;
  }

  private class MyRayBack implements RayCastCallback {
    private boolean show;
    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
      ManiObject o = (ManiObject) fixture.getBody().getUserData();
      if (myHero == o) {
        return -1;
      }
      show = true;
      return 0;
    }
  }
}
