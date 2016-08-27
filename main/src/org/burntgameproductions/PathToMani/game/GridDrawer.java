

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.ManiColor;

public class GridDrawer {

  public GridDrawer(TextureManager textureManager) {
  }

  public void draw(GameDrawer drawer, ManiGame game, float gridSz, TextureAtlas.AtlasRegion tex) {
    ManiCam cam = game.getCam();
    float lw = 4 * cam.getRealLineWidth();
    Vector2 camPos = cam.getPos();
    float viewDist = cam.getViewDist(cam.getRealZoom());
    float x = (int) ((camPos.x - viewDist) / gridSz) * gridSz;
    float y = (int) ((camPos.y - viewDist) / gridSz) * gridSz;
    int count = (int)(viewDist * 2 / gridSz);
    Color col = ManiColor.UI_INACTIVE;
    for (int i = 0; i < count; i++) {
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, 0, col);
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, 90, col);
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, 180, col);
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, -90, col);
      x += gridSz;
      y += gridSz;
    }
  }
}
