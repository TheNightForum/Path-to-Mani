

package org.burntgameproductions.PathToMani.game.item;

import java.util.List;

public class ItemConfig {
  public final List<ManiItem> examples;
  public final int amt;
  public final float chance;

  public ItemConfig(List<ManiItem> examples, int amt, float chance) {
    this.examples = examples;
    this.amt = amt;
    this.chance = chance;
  }
}
