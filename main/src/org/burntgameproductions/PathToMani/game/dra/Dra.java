

package org.burntgameproductions.PathToMani.game.dra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.game.GameDrawer;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ManiObject;

//TODO Dra?
public interface Dra {
  Texture getTex0();
  TextureAtlas.AtlasRegion getTex();
  DraLevel getLevel();
  // called on every update from manager
  void update(ManiGame game, ManiObject o);
  // called on every draw from manager. after that, this dra should be able to return correct pos & radius
  void prepare(ManiObject o);
  Vector2 getPos();
  Vector2 getRelPos();
  float getRadius();
  void draw(GameDrawer drawer, ManiGame game);
  boolean isEnabled();
  boolean okToRemove();
}
