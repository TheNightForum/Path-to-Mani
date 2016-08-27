

/*
 * No License as of yet.
 */
package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Input;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class ControlsScreen implements ManiUiScreen {
  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myBackCtrl;
  private final ManiUiControl myControlTypeCtrl;
  private final ManiUiControl inputMapCtrl;

  public ControlsScreen(MenuLayout menuLayout, GameOptions gameOptions) {

    myControls = new ArrayList<ManiUiControl>();

    myControlTypeCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.C);
    myControlTypeCtrl.setDisplayName("Control Type");
    myControls.add(myControlTypeCtrl);

    inputMapCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.M);
    inputMapCtrl.setDisplayName("Edit");
    myControls.add(inputMapCtrl);

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
    ManiInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    GameOptions options = cmp.getOptions();

    int ct = cmp.getOptions().controlType;
    String ctName = "Keyboard";
    if (ct == GameOptions.CONTROL_MIXED) ctName = "KB + Mouse";
    if (ct == GameOptions.CONTROL_MOUSE) ctName = "Mouse";
    if (ct == GameOptions.CONTROL_CONTROLLER) ctName = "Controller";
    myControlTypeCtrl.setDisplayName("Input: " + ctName);
    if (myControlTypeCtrl.isJustOff()) {
      cmp.getOptions().advanceControlType(false);
    }

    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.options);
    }


    if (inputMapCtrl.isJustOff()) {
      if (ct == GameOptions.CONTROL_MIXED) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapMixedScreen);
      } else if (ct == GameOptions.CONTROL_KB) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapKeyboardScreen);
      } else if (ct == GameOptions.CONTROL_CONTROLLER) {
        screens.inputMapScreen.setOperations(screens.inputMapScreen.inputMapControllerScreen);
      }
      im.setScreen(cmp, screens.inputMapScreen);
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
