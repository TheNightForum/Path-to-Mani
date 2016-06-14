package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.game.DebugOptions;
import org.burntgameproductions.PathToMani.game.sound.MusicManager;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlayScreen implements ManiUiScreen {
  public static final float CREDITS_BTN_W = .15f;
  public static final float CREDITS_BTN_H = .07f;

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myTutCtrl;
  private final ManiUiControl myNewCtrl;
  private final ManiUiControl myLoadCtrl;
  private final ManiUiControl myBackCtrl;
  //logos
  private final TextureAtlas.AtlasRegion logo;
  private final TextureAtlas.AtlasRegion newyears;
  private final TextureAtlas.AtlasRegion aprilfools;
  private final TextureAtlas.AtlasRegion australiaday;

  //boolean's for date checker.
  private boolean isNewYears;
  private boolean isAustraliaDay;
  private boolean isAprilFools;

  private final boolean isMobile;
  GameOptions gameOptions;

  public PlayScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    isMobile = mobile;
    myControls = new ArrayList<ManiUiControl>();
    this.gameOptions = gameOptions;

    myTutCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.T);
    myTutCtrl.setDisplayName("Tutorial");
    myControls.add(myTutCtrl);

    myNewCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.N);
    myNewCtrl.setDisplayName("New Game");
    myControls.add(myNewCtrl);

    myLoadCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.L);
    myLoadCtrl.setDisplayName("Load Game");
    myControls.add(myLoadCtrl);

    myBackCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.B);
    myBackCtrl.setDisplayName("Back");
    myControls.add(myBackCtrl);

    //myTitleTex = textureManager.getTex("ui/title", null);
    /**
     *The reason why we are doing it like this is so it doesnt have to use a texture atlas.
     */
    //Normal
    FileHandle imageFile = FileManager.getInstance().getImagesDirectory().child("logo.png");
    logo = textureManager.getTexture(imageFile);
    //Australia day.
    FileHandle imageFile1 = FileManager.getInstance().getImagesDirectory().child("australiaday.png");
    australiaday = textureManager.getTexture(imageFile1);
    //April fools.
    FileHandle imageFile2 = FileManager.getInstance().getImagesDirectory().child("aprilfools.png");
    aprilfools = textureManager.getTexture(imageFile2);
    //New years.
    FileHandle imageFile3 = FileManager.getInstance().getImagesDirectory().child("newyears.png");
    newyears = textureManager.getTexture(imageFile3);

    //Date changer.
    Calendar var1 = Calendar.getInstance();
    if (var1.get(2) + 1 == 04 && var1.get(5) >= 01 && var1.get(5) <= 03)
    {
      this.isAprilFools = true;
    }
    else if (var1.get(2) + 1 == 01 && var1.get(5) >= 26 && var1.get(5) <= 28)
    {
      this.isAustraliaDay = true;
    }
    else if (var1.get(2) + 1 == 01 && var1.get(5) >= 01 && var1.get(5) <= 07)
    {
      this.isNewYears = true;
    }
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
    if (cmp.getOptions().controlType == GameOptions.CONTROL_CONTROLLER) {
      myTutCtrl.setEnabled(false);
    } else {
      myTutCtrl.setEnabled(true);
    }
    if (myNewCtrl.isJustOff()) {
      if (!myLoadCtrl.isEnabled()) {
        cmp.loadNewGame(false, false);
      } else {
        im.setScreen(cmp, screens.newShip);
      }
    }
    if (myLoadCtrl.isJustOff()) {
      cmp.loadNewGame(false, true);
      return;
    }
    if (myBackCtrl.isJustOff()) {
      im.setScreen(cmp, screens.main);
      return;
    }
  }

  @Override
  public boolean isCursorOnBg(ManiInputManager.Ptr ptr) {
    return false;
  }

  @Override
  public void onAdd(ManiApplication cmp) {
    MusicManager.getInstance().PlayMenuMusic(gameOptions);
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
    /**We are now loading the correct image for the date**/
    if (this.isNewYears)
    {
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(newyears, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
    }
    else if (this.isAustraliaDay)
    {
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(australiaday, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
    }
    else if (this.isAprilFools)
    {
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(aprilfools, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
    }
    else
    {
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(logo, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, SolColor.W);
    }
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
