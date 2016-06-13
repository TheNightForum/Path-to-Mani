

package org.burntgameproductions.PathToMani.game.dra;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.*;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.common.Consumed;
import org.burntgameproductions.PathToMani.game.planet.Planet;

import java.util.List;

public class DrasObject implements ManiObject {
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final RemoveController myRemoveController;
  private final boolean myHideOnPlanet;
  private final Vector2 myMoveDiff;
  private final List<Dra> myDras;
  private final boolean myTemporary;

  private float myMaxFadeTime;
  private float myFadeTime;

  public DrasObject(List<Dra> dras, @Consumed Vector2 pos, @Consumed Vector2 spd, RemoveController removeController, boolean temporary, boolean hideOnPlanet) {
    myDras = dras;
    myPos = pos;
    mySpd = spd;
    myRemoveController = removeController;
    myHideOnPlanet = hideOnPlanet;
    myMoveDiff = new Vector2();
    myTemporary = temporary;

    myMaxFadeTime = -1;
    myFadeTime = -1;
  }

  @Override
  public void update(ManiGame game) {
    myMoveDiff.set(mySpd);
    float ts = game.getTimeStep();
    myMoveDiff.scl(ts);
    myPos.add(myMoveDiff);
    if (myHideOnPlanet) {
      Planet np = game.getPlanetMan().getNearestPlanet();
      Vector2 npPos = np.getPos();
      float npgh = np.getGroundHeight();
      DraMan draMan = game.getDraMan();
      for (int i = 0, myDrasSize = myDras.size(); i < myDrasSize; i++) {
        Dra dra = myDras.get(i);
        if (!(dra instanceof RectSprite)) continue;
        if (!draMan.isInCam(dra)) continue;
        Vector2 draPos = dra.getPos();
        float gradSz = .25f * Const.ATM_HEIGHT;
        float distPerc = (draPos.dst(npPos) - npgh - Const.ATM_HEIGHT) / gradSz;
        distPerc = ManiMath.clamp(distPerc);
        ((RectSprite) dra).tint.a = distPerc;
      }
    } else if (myMaxFadeTime > 0) {
      myFadeTime -= ts;
      float tintPerc = myFadeTime / myMaxFadeTime;
      for (int i = 0, myDrasSize = myDras.size(); i < myDrasSize; i++) {
        Dra dra = myDras.get(i);
        if (!(dra instanceof RectSprite)) continue;
        RectSprite rs = (RectSprite) dra;
        rs.tint.a = ManiMath.clamp(tintPerc * rs.baseAlpha);
      }

    }
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    if (myMaxFadeTime > 0 && myFadeTime <= 0) return true;
    if (myTemporary) {
      boolean rem = true;
      for (int i = 0, myDrasSize = myDras.size(); i < myDrasSize; i++) {
        Dra dra = myDras.get(i);
        if (!dra.okToRemove()) {
          rem = false;
          break;
        }
      }
      if (rem) return true;
    }
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public void onRemove(ManiGame game) {
  }

  @Override
  public void receiveDmg(float dmg, ManiGame game, Vector2 pos, DmgType dmgType) {
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveForce(Vector2 force, ManiGame game, boolean acc) {
  }

  @Override
  public Vector2 getPosition() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return myTemporary ? null : new FarDras(myDras, myPos, mySpd, myRemoveController, myHideOnPlanet);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public float getAngle() {
    return 0;
  }

  @Override
  public Vector2 getSpd() {
    return null;
  }

  @Override
  public void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                            ManiGame game, Vector2 collPos)
  {
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return null;
  }

  @Override
  public boolean hasBody() {
    return false;
  }

  public void fade(float fadeTime) {
    myMaxFadeTime = fadeTime;
    myFadeTime = fadeTime;
  }
}
