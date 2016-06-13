

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.common.Nullable;

import java.util.List;

public interface ManiObject {
  void update(ManiGame game);
  boolean shouldBeRemoved(ManiGame game);
  void onRemove(ManiGame game);
  void receiveDmg(float dmg, ManiGame game, @Nullable Vector2 pos, DmgType dmgType);
  boolean receivesGravity();
  void receiveForce(Vector2 force, ManiGame game, boolean acc);
  Vector2 getPosition();
  FarObj toFarObj();
  List<Dra> getDras();
  float getAngle();
  Vector2 getSpd();
  void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse, ManiGame game,
                     Vector2 collPos);
  String toDebugString();
  Boolean isMetal();
  boolean hasBody();
}
