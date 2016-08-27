

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.ui.FontSize;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.planet.PlanetManager;
import org.burntgameproductions.PathToMani.game.planet.ManiSystem;

public class ZoneNameAnnouncer {
  private static final float FADE_TIME = 4f;
  private String myZone;
  private final Color myCol = new Color(1, 1, 1, 1);
  private String myText;

  public void update(ManiGame game) {
    PlanetManager pm = game.getPlanetMan();
    String zone = null;
    String pref = null;
    Vector2 camPos = game.getCam().getPos();
    Planet p = pm.getNearestPlanet();
    if (p.getPos().dst(camPos) < p.getFullHeight()) {
      zone = p.getName();
      pref = "Planet";
    } else {
      ManiSystem s = pm.getNearestSystem(camPos);
      if (s.getPos().dst(camPos) < s.getRadius()) {
        zone = s.getName();
        pref = "System";
      }
    }
    boolean reset = zone != null && !zone.equals(myZone);
    myZone = zone;
    if (reset) {
      myCol.a = 1f;
      myText = pref + ":\n" + myZone;
    } else if (myCol.a > 0) {
      myCol.a -= Const.REAL_TIME_STEP / FADE_TIME;
    }
  }

  public void drawText(UiDrawer uiDrawer) {
    if (myCol.a <= 0) return;
    uiDrawer.drawString(myText, uiDrawer.r/2, .15f, FontSize.MENU * 1.5f, true, myCol);
  }
}
