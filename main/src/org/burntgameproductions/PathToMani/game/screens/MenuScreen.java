

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
  private final ManiUiControl myRespawnCtrl;
  private final ManiUiControl myExitCtrl;
  private final ManiUiControl myCloseCtrl;
  private final ManiUiControl myOptionsCtrl;

  public MenuScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myCloseCtrl = new ManiUiControl(menuLayout.buttonRect(-1,1), true);
    myCloseCtrl.setDisplayName("Resume");
    myControls.add(myCloseCtrl);

    myRespawnCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true);
    myRespawnCtrl.setDisplayName("Respawn");
    myControls.add(myRespawnCtrl);

    myOptionsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true);
    myOptionsCtrl.setDisplayName("Options");
    myControls.add(myOptionsCtrl);

    myExitCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true);
    myExitCtrl.setDisplayName("Exit");
    myControls.add(myExitCtrl);
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

    if (myCloseCtrl.isJustOff()) {
      g.setPaused(false);
      im.setScreen(cmp, g.getScreens().mainScreen);
    }

    if (myRespawnCtrl.isJustOff()) {
      g.respawn();
      im.setScreen(cmp, g.getScreens().mainScreen);
      g.setPaused(false);
    }

    if (myOptionsCtrl.isJustOff()) {
      g.setPaused(true);
      return;
    }

    if (myExitCtrl.isJustOff()) {
      cmp.finishGame();
    }
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
