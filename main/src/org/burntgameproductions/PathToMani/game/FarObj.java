

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.math.Vector2;

public interface FarObj {
  boolean shouldBeRemoved(ManiGame game);
  ManiObject toObj(ManiGame game);
  void update(ManiGame game);
  float getRadius();
  Vector2 getPos();
  String toDebugString();
  boolean hasBody();
}
