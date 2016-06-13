

package org.burntgameproductions.PathToMani.game.screens;

import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.item.ItemContainer;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;

public interface InventoryOperations extends ManiUiScreen {
  ItemContainer getItems(ManiGame game);
  boolean isUsing(ManiGame game, ManiItem item);
  float getPriceMul();
  String getHeader();
}
