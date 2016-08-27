

package org.burntgameproductions.PathToMani.menu;

import org.burntgameproductions.PathToMani.ui.*;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.common.ManiColor;

import java.util.ArrayList;
import java.util.List;

public class LoadingScreen implements ManiUiScreen {
  private final ArrayList<ManiUiControl> myControls;
  private boolean myTut;
  private boolean myUsePrevShip;

  public LoadingScreen() {
    myControls = new ArrayList<ManiUiControl>();
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void onAdd(ManiApplication cmp) {
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    cmp.startNewGame(myTut, myUsePrevShip);
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void blurCustom(ManiApplication cmp) {
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
    uiDrawer.drawString("Loading...", uiDrawer.r/2, .5f, FontSize.MENU, true, ManiColor.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  public void setMode(boolean tut, boolean usePrevShip) {
    myTut = tut;
    myUsePrevShip = usePrevShip;
  }
}
