

package org.burntgameproductions.PathToMani.game.dra;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.RemoveController;
import org.burntgameproductions.PathToMani.game.ManiGame;

import java.util.List;

public class FarDras implements FarObj {
  private final List<Dra> myDras;
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final RemoveController myRemoveController;
  private final float myRadius;
  private final boolean myHideOnPlanet;

  public FarDras(List<Dra> dras, Vector2 pos, Vector2 spd, RemoveController removeController,
    boolean hideOnPlanet) {
    myDras = dras;
    myPos = pos;
    mySpd = spd;
    myRemoveController = removeController;
    myRadius = DraMan.radiusFromDras(myDras);
    myHideOnPlanet = hideOnPlanet;
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public ManiObject toObj(ManiGame game) {
    return new DrasObject(myDras, myPos, mySpd, myRemoveController, false, myHideOnPlanet);
  }

  @Override
  public void update(ManiGame game) {
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

  public List<Dra> getDras() {
    return myDras;
  }
}
