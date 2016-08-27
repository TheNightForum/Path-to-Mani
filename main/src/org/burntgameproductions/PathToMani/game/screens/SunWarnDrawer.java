

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public class SunWarnDrawer extends WarnDrawer {

  public SunWarnDrawer(float r) {
    super(r, "Sun Near");
  }

  public boolean shouldWarn(ManiGame game) {
    ManiShip hero = game.getHero();
    if (hero == null) return false;
    Vector2 pos = hero.getPosition();
    float toCenter = game.getPlanetMan().getNearestSystem(pos).getPos().dst(pos);
    return toCenter < Const.SUN_RADIUS;
  }
}
