

package org.burntgameproductions.PathToMani.game.screens;

import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public class DmgWarnDrawer extends WarnDrawer {

  public DmgWarnDrawer(float r) {
    super(r, "Heavily Damaged");
  }

  @Override
  protected boolean shouldWarn(ManiGame game) {
    ManiShip hero = game.getHero();
    if (hero == null) return false;
    float l = hero.getLife();
    int ml = hero.getHull().config.getMaxLife();
    return l < ml * .3f;
  }
}
