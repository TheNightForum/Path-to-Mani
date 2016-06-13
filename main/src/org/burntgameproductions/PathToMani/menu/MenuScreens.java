

package org.burntgameproductions.PathToMani.menu;

import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.ui.ManiLayouts;

public class MenuScreens {
  public final MainScreen main;
  public final OptionsScreen options;
  public final InputMapScreen inputMapScreen;
  public final ResolutionScreen resolutionScreen;
  public final CreditsScreen credits;
  public final LoadingScreen loading;
  public final NewGameScreen newGame;
  public final NewShipScreen newShip;

  public MenuScreens(ManiLayouts layouts, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    MenuLayout menuLayout = layouts.menuLayout;
    main = new MainScreen(menuLayout, textureManager, mobile, r, gameOptions);
    options = new OptionsScreen(menuLayout, gameOptions);
    inputMapScreen = new InputMapScreen(r, gameOptions);
    resolutionScreen = new ResolutionScreen(menuLayout, gameOptions);
    credits = new CreditsScreen(r, gameOptions);
    loading = new LoadingScreen();
    newGame = new NewGameScreen(menuLayout, gameOptions);
    newShip = new NewShipScreen(menuLayout, gameOptions);
  }

}
