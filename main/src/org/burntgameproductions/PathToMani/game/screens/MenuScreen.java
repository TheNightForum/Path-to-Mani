

package org.burntgameproductions.PathToMani.game.screens;

import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.common.SolColor;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.menu.MenuLayout;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;

import java.util.ArrayList;
import java.util.List;

public class MenuScreen implements ManiUiScreen {
  private final List<ManiUiControl> myControls;
  private final ManiUiControl myCloseCtrl;
  private final ManiUiControl myExitCtrl;
  private final ManiUiControl myRespawnCtrl;
  private final ManiUiControl myVolCtrl;

  public MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myVolCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true);
    myVolCtrl.setDisplayName("Vol");
    myControls.add(myVolCtrl);
    myRespawnCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true);
    myRespawnCtrl.setDisplayName("Respawn");
    myControls.add(myRespawnCtrl);
    myExitCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);
    myCloseCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyClose());
    myCloseCtrl.setDisplayName("Resume");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiGame g = cmp.getGame();
    g.setPaused(true);
    ManiInputManager im = cmp.getInputMan();
    GameOptions options = cmp.getOptions();
    myVolCtrl.setDisplayName("Volume: " + getVolName(options));
    if (myVolCtrl.isJustOff()) {
      options.advanceVolMul();
    }
    if (myRespawnCtrl.isJustOff()) {
      g.respawn();
      im.setScreen(cmp, g.getScreens().mainScreen);
      g.setPaused(false);
    }
    if (myExitCtrl.isJustOff()) {
      cmp.finishGame();
    }
    if (myCloseCtrl.isJustOff()) {
      g.setPaused(false);
      im.setScreen(cmp, g.getScreens().mainScreen);
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
    uiDrawer.draw(uiDrawer.filler, SolColor.UI_BG);
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
    return true;
  }

  @Override
  public void onAdd(ManiApplication cmp) {

  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
