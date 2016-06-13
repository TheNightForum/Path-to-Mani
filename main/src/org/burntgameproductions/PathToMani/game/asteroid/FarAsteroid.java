

package org.burntgameproductions.PathToMani.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.RemoveController;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class FarAsteroid implements FarObj {
  private final Vector2 myPos;
  private final float myAngle;
  private final RemoveController myRemoveController;
  private final float mySz;
  private final Vector2 mySpd;
  private final float myRotSpd;
  private final TextureAtlas.AtlasRegion myTex;

  public FarAsteroid(TextureAtlas.AtlasRegion tex, Vector2 pos, float angle, RemoveController removeController,
    float sz, Vector2 spd, float rotSpd)
  {
    myTex = tex;
    myPos = pos;
    myAngle = angle;
    myRemoveController = removeController;
    mySz = sz;
    mySpd = spd;
    myRotSpd = rotSpd;
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public ManiObject toObj(ManiGame game) {
    return game.getAsteroidBuilder().build(game, myPos, myTex, mySz, myAngle, myRotSpd, mySpd, myRemoveController);
  }

  @Override
  public void update(ManiGame game) {
  }

  @Override
  public float getRadius() {
    return mySz;
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
    return true;
  }
}