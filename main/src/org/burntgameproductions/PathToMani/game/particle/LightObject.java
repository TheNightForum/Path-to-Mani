

package org.burntgameproductions.PathToMani.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.DmgType;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.ManiGame;

import java.util.ArrayList;
import java.util.List;

public class LightObject implements ManiObject {

  private final LightSrc myLightSrc;
  private final ArrayList<Dra> myDras;
  private final Vector2 myPos;

  // consumes pos
  public LightObject(ManiGame game, float sz, boolean hasHalo, float intensity, Vector2 pos, float fadeTime, Color col) {
    myPos = pos;
    myLightSrc = new LightSrc(game, sz, hasHalo, intensity, new Vector2(), col);
    myLightSrc.setFadeTime(fadeTime);
    myLightSrc.setWorking();
    myDras = new ArrayList<Dra>();
    myLightSrc.collectDras(myDras);
  }

  @Override
  public void update(ManiGame game) {
    myLightSrc.update(false, 0, game);
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return myLightSrc.isFinished();
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
    return null;
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
}
