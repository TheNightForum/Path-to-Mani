

package org.burntgameproductions.PathToMani.game.sound;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.Nullable;
import org.burntgameproductions.PathToMani.game.GameDrawer;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.ManiGame;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DebugHintDrawer {
  private final Map<ManiObject, DebugHint> myTracedNotes;
  private final Map<Vector2, DebugHint> myFreeNotes;

  public DebugHintDrawer() {
    myTracedNotes = new HashMap<ManiObject, DebugHint>();
    myFreeNotes = new HashMap<Vector2, DebugHint>();
  }

  public void add(@Nullable ManiObject owner, Vector2 pos, String value) {
    DebugHint dh;
    if (owner == null) {
      dh = myFreeNotes.get(pos);
      if (dh == null) {
        dh = new DebugHint(null, pos);
        myFreeNotes.put(pos, dh);
      }
    } else {
      dh = myTracedNotes.get(owner);
      if (dh == null) {
        dh = new DebugHint(owner, owner.getPosition());
        myTracedNotes.put(owner, dh);
      }
    }
    dh.add(value);
  }

  public void update(ManiGame game) {
    updateEach(game, myTracedNotes.values().iterator());
    updateEach(game, myFreeNotes.values().iterator());
  }

  private void updateEach(ManiGame game, Iterator<DebugHint> it) {
    while (it.hasNext()) {
      DebugHint n = it.next();
      n.update(game);
      if (n.shouldRemove()) it.remove();
    }
  }

  public void draw(GameDrawer drawer, ManiGame game) {
    for (DebugHint n : myTracedNotes.values()) n.draw(drawer, game);
    for (DebugHint n : myFreeNotes.values()) n.draw(drawer, game);
  }

}
