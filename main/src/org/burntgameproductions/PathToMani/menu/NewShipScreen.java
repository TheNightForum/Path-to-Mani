

package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Input;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ui.*;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.common.SolColor;

import java.util.ArrayList;
import java.util.List;

public class NewShipScreen implements ManiUiScreen {
  private final List<ManiUiControl> myControls;
  private final ManiUiControl myOkCtrl;
  public final ManiUiControl myCancelCtrl;

  public NewShipScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();
    myOkCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.H);
    myOkCtrl.setDisplayName("OK");
    myControls.add(myOkCtrl);

    myCancelCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myCancelCtrl.setDisplayName("Cancel");
    myControls.add(myCancelCtrl);
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
    if (myCancelCtrl.isJustOff()) {
      cmp.getInputMan().setScreen(cmp, cmp.getMenuScreens().newGame);
      return;
    }
    if (myOkCtrl.isJustOff()) {
      cmp.loadNewGame(false, false);
    }
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
    uiDrawer.drawString("This will erase your previous ship", .5f * uiDrawer.r, .3f, FontSize.MENU, true, SolColor.W);
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }
}
