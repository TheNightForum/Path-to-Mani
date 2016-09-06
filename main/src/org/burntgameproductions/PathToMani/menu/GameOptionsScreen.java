

/*
 * No License as of yet.
 */
package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Input;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class GameOptionsScreen implements ManiUiScreen {
  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBackCtrl;
  private final ManiUiControl myResoCtrl;
  private final ManiUiControl myVolCtrl;
  private final ManiUiControl myControlCtrl;

  public GameOptionsScreen(MenuLayout menuLayout, GameOptions gameOptions) {

    myControls = new ArrayList<ManiUiControl>();

    myVolCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 0), true);
    myVolCtrl.setDisplayName("Vol");
    myControls.add(myVolCtrl);

    myResoCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myControlCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.C);
    myControlCtrl.setDisplayName("Controls");
    myControls.add(myControlCtrl);

    myBackCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myBackCtrl.setDisplayName("Back");
    myControls.add(myBackCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame g = cmp.getGame();
    ManiInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    GameOptions options = cmp.getOptions();
    myVolCtrl.setDisplayName("Volume: " + getVolName(options));
    if (myVolCtrl.isJustOff()) {
      options.advanceVolMul();
    }

    if (myResoCtrl.isJustOff()) {
      im.setScreen(cmp, screens.resolutionScreen);
    }

    if (myControlCtrl.isJustOff()) {
      im.setScreen(cmp, screens.controlsScreen);
    }

    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, g.getScreens().menuScreen);
    }

  }
  private String getVolName(GameOptions options) {
    float volMul = options.volMul;
    if (volMul == 0) return "Off";
    if (volMul < .4f) return "Low";
    if (volMul < .7f) return "High";
    return "Max";
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
