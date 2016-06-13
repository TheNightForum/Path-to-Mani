

package org.burntgameproductions.PathToMani.ui;

import org.burntgameproductions.PathToMani.game.screens.RightPaneLayout;
import org.burntgameproductions.PathToMani.menu.MenuLayout;

public class ManiLayouts {
  public final RightPaneLayout rightPaneLayout;
  public final MenuLayout menuLayout;

  public ManiLayouts(float r) {
    rightPaneLayout = new RightPaneLayout(r);
    menuLayout = new MenuLayout(r);
  }
}
