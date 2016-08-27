

package org.burntgameproductions.PathToMani.game.ship;

import org.burntgameproductions.PathToMani.game.item.ManiItem;

public interface AbilityConfig {
  public ShipAbility build();
  public ManiItem getChargeExample();
  public float getRechargeTime();
  void appendDesc(StringBuilder sb);
}
