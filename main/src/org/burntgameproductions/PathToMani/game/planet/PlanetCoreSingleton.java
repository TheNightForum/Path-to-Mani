

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.SolColor;
import org.burntgameproductions.PathToMani.game.GameDrawer;
import org.burntgameproductions.PathToMani.game.ManiCam;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class PlanetCoreSingleton {
  private final TextureAtlas.AtlasRegion myTex;

  public PlanetCoreSingleton(TextureManager textureManager) {
    myTex = textureManager.getTex("planetStarCommons/planetCore", null);
  }


  public void draw(ManiGame game, GameDrawer drawer) {
    ManiCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    Planet p = game.getPlanetMan().getNearestPlanet();
    Vector2 pPos = p.getPos();
    float toCamLen = camPos.dst(pPos);
    float vd = cam.getViewDist();
    float gh = p.getMinGroundHeight();
    if (toCamLen < gh + vd) {
      float sz = gh;
      drawer.draw(myTex, sz *2, sz *2, sz, sz, pPos.x, pPos.y, p.getAngle(), SolColor.W);
    }
  }
}
