

package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.SolColor;
import org.burntgameproductions.PathToMani.game.DebugOptions;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.sound.MusicManager;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements ManiUiScreen {
  public static final float CREDITS_BTN_W = .15f;
  public static final float CREDITS_BTN_H = .07f;

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myPlayCtrl;
  private final ManiUiControl myOptionsCtrl;
  private final ManiUiControl myCreditsCtrl;
  private final ManiUiControl myQuitCtrl;
  //private final TextureAtlas.AtlasRegion myTitleTex;
  private final boolean isMobile;
  GameOptions gameOptions;

  public MainScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    isMobile = mobile;
    myControls = new ArrayList<ManiUiControl>();
    this.gameOptions = gameOptions;

    myPlayCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.P);
    myPlayCtrl.setDisplayName("Play");
    myControls.add(myPlayCtrl);

    myOptionsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.O);
    myOptionsCtrl.setDisplayName("Options");
    myControls.add(myOptionsCtrl);

    myCreditsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.C);
    myCreditsCtrl.setDisplayName("Credits");
    myControls.add(myCreditsCtrl);

    myQuitCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.Q);
    myQuitCtrl.setDisplayName("Quit");
    myControls.add(myQuitCtrl);

    //myTitleTex = textureManager.getTex("ui/title", null);
  }

  public static Rectangle creditsBtnRect(float r) {
    return new Rectangle(r - CREDITS_BTN_W, 1 - CREDITS_BTN_H, CREDITS_BTN_W, CREDITS_BTN_H);
  }

  public List<ManiUiControl> getControls() {
    return myControls;
  }

  @Override
  public void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside) {
    ManiInputManager im = cmp.getInputMan();
    MenuScreens screens = cmp.getMenuScreens();
    if (myPlayCtrl.isJustOff()) {
      im.setScreen(cmp, screens.playScreen);
      return;
    }
    if (myOptionsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.options);
      return;
    }
    if (myCreditsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.credits);
      return;
    }
    if (myQuitCtrl.isJustOff()) {
      // Save the settings on exit, but not on mobile as settings don't exist there.
      if (isMobile == false) {
        cmp.getOptions().save();
      }
      Gdx.app.exit();
      return;
    }


  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {
  //  MusicManager.getInstance().PlayMenuMusic(gameOptions);
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
    float sz = .55f;
    //if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(myTitleTex, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, SolColor.W);
  }

  @Override
  public void drawText(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public boolean reactsToClickOutside() {
    return false;
  }

  @Override
  public void blurCustom(ManiApplication cmp) {

  }
}
