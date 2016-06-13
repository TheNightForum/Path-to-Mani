

package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Gdx;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ui.*;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.common.ManiColor;

import java.util.ArrayList;
import java.util.List;

public class ResolutionScreen implements ManiUiScreen {

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myCloseCtrl;
  private final ManiUiControl myResoCtrl;
  private final ManiUiControl myFsCtrl;

  public ResolutionScreen(MenuLayout menuLayout, GameOptions gameOptions) {
    myControls = new ArrayList<ManiUiControl>();

    myResoCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true);
    myResoCtrl.setDisplayName("Resolution");
    myControls.add(myResoCtrl);

    myFsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true);
    myFsCtrl.setDisplayName("Fullscreen");
    myControls.add(myFsCtrl);

    myCloseCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, gameOptions.getKeyEscape());
    myCloseCtrl.setDisplayName("Back");
    myControls.add(myCloseCtrl);
  }

  @Override
  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiInputManager im = cmp.getInputMan();
    if (myCloseCtrl.isJustOff()) {
      GameOptions options = cmp.getOptions();
      Gdx.graphics.setDisplayMode(options.x, options.y, options.fullscreen);
      im.setScreen(cmp, cmp.getMenuScreens().options);
      return;
    }

    GameOptions options = cmp.getOptions();
    myResoCtrl.setDisplayName(options.x + "x" + options.y);
    if (myResoCtrl.isJustOff()) {
      options.advanceReso();
    }
    myFsCtrl.setDisplayName(options.fullscreen ? "Fullscreen" : "Windowed");
    if (myFsCtrl.isJustOff()) {
      options.advanceFullscreen();
    }
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {

  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
    uiDrawer.drawString("Click 'Back' to apply changes", .5f * uiDrawer.r, .3f, FontSize.MENU, true, ManiColor.W);
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
