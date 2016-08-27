

package org.burntgameproductions.PathToMani.game.ship;

import org.burntgameproductions.PathToMani.game.AbilityCommonConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;

public interface ShipAbility {
  boolean update(ManiGame game, ManiShip owner, boolean tryToUse);
  public AbilityConfig getConfig();
  AbilityCommonConfig getCommonConfig();
  float getRadius();
}
