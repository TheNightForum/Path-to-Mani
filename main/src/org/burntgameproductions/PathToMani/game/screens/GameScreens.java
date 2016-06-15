

package org.burntgameproductions.PathToMani.game.screens;

import org.burntgameproductions.PathToMani.ui.ManiLayouts;
import org.burntgameproductions.PathToMani.ManiApplication;

public class GameScreens {
  public final MainScreen mainScreen;
  public final MapScreen mapScreen;
  public final MenuScreen menuScreen;
  public final InventoryScreen inventoryScreen;
  public final TalkScreen talkScreen;

  //TODO: Dan here it is...

  public GameScreens(float r, ManiApplication cmp) {
    ManiLayouts layouts = cmp.getLayouts();
    RightPaneLayout rightPaneLayout = layouts.rightPaneLayout;
    mainScreen = new MainScreen(r, rightPaneLayout, cmp);
    mapScreen = new MapScreen(rightPaneLayout, cmp.isMobile(), r, cmp.getOptions());
    menuScreen = new MenuScreen(layouts.menuLayout, cmp.getOptions());
    inventoryScreen = new InventoryScreen(r, cmp.getOptions());
    talkScreen = new TalkScreen(layouts.menuLayout, cmp.getOptions());
  }

}
