package org.burntgameproductions.PathToMani.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.game.DebugOptions;
import org.burntgameproductions.PathToMani.game.sound.MusicManager;
import org.burntgameproductions.PathToMani.ui.ManiUiScreen;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainScreen implements ManiUiScreen {
  public static final float CREDITS_BTN_W = .15f;
  public static final float CREDITS_BTN_H = .07f;

  private final ArrayList<ManiUiControl> myControls;
  private final ManiUiControl myPlayCtrl;
  private final ManiUiControl myOptionsCtrl;
  private final ManiUiControl myCreditsCtrl;
  private final ManiUiControl myQuitCtrl;

  /**We have these listed here as a texture so then we can use them later on.**/
  private final TextureAtlas.AtlasRegion logo;
  private final TextureAtlas.AtlasRegion aprilfools;
  private final TextureAtlas.AtlasRegion australiaday;
  private final TextureAtlas.AtlasRegion newyears;

  /**Boolean's for date checker**/
  private boolean isNewYears;
  private boolean isAustraliaDay;
  private boolean isAprilFools;

  private final boolean isMobile;
  GameOptions gameOptions;

  public MainScreen(MenuLayout menuLayout, TextureManager textureManager, boolean mobile, float r, GameOptions gameOptions) {
    isMobile = mobile;
    myControls = new ArrayList<ManiUiControl>();
    this.gameOptions = gameOptions;

    /**Play button**/
    myPlayCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 1), true, Input.Keys.P);
    myPlayCtrl.setDisplayName("Play");
    myControls.add(myPlayCtrl);

    /**Options button**/
    myOptionsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 2), true, Input.Keys.O);
    myOptionsCtrl.setDisplayName("Options");
    myControls.add(myOptionsCtrl);

    /**Credits button**/
    myCreditsCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 3), true, Input.Keys.C);
    myCreditsCtrl.setDisplayName("Credits");
    myControls.add(myCreditsCtrl);

    /**Quit button**/
    myQuitCtrl = new ManiUiControl(menuLayout.buttonRect(-1, 4), true, Input.Keys.Q);
    myQuitCtrl.setDisplayName("Quit");
    myControls.add(myQuitCtrl);

    /**
     * Logos for date changer are loaded here.
     **/
    /**Normal.**/
    FileHandle imageFile = FileManager.getInstance().getImagesDirectory().child("logo.png");
    logo = textureManager.getTexture(imageFile);
    /**Australiad Day.**/
    FileHandle imageFile2 = FileManager.getInstance().getImagesDirectory().child("australiaday.png");
    australiaday = textureManager.getTexture(imageFile2);
    /**April Fools.**/
    FileHandle imageFile3 = FileManager.getInstance().getImagesDirectory().child("aprilfools.png");
    aprilfools = textureManager.getTexture(imageFile3);
    /**New years.**/
    FileHandle imageFile4 = FileManager.getInstance().getImagesDirectory().child("newyears.png");
    newyears = textureManager.getTexture(imageFile4);

    /**Calendar**/
    Calendar var1 = Calendar.getInstance();
    if (var1.get(2) + 1 == 04 && var1.get(5) >= 01 && var1.get(5) <= 03)
    {/**This ^^^^ here... We want to display the logo between these days of this month.**/
      this.isAprilFools = true;
    }
    else if (var1.get(2) + 1 == 01 && var1.get(5) >= 26 && var1.get(5) <= 28)
    {/**This ^^^^ here... We want to display the logo between these days of this month.**/
      this.isAustraliaDay = true;
    }
    else if (var1.get(2) + 1 == 01 && var1.get(5) >= 01 && var1.get(5) <= 07)
    {/**This ^^^^ here... We want to display the logo between these days of this month.**/
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

    /**What to do when "Play" button is pressed.**/
    if (myPlayCtrl.isJustOff()) {
      im.setScreen(cmp, screens.playScreen);
      return;
    }

    /**What to do when "Options" button is pressed.**/
    if (myOptionsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.options);
      return;
    }

    /**What to do when "Credits" button is pressed.**/
    if (myCreditsCtrl.isJustOff()) {
      im.setScreen(cmp, screens.credits);
      return;
    }

    /**What to do when "Quit" button is pressed.**/
    if (myQuitCtrl.isJustOff()) {
      /** Save the settings on exit, but not on mobile as settings don't exist there.**/
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
    MusicManager.getInstance().PlayMenuMusic(gameOptions);
  }

  @Override
  public void drawBg(UiDrawer uiDrawer, ManiApplication cmp) {
  }

  @Override
  public void drawImgs(UiDrawer uiDrawer, ManiApplication cmp) {
    /**We are now loading the correct image for the date**/
    if (this.isNewYears)
    {/**Load this logo when the date on the players computer is the same as the one we set above.**/
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(newyears, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
    }
    else if (this.isAustraliaDay)
    {/**Load this logo when the date on the players computer is the same as the one we set above.**/
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(australiaday, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
    }
    else if (this.isAprilFools)
    {/**Load this logo when the date on the players computer is the same as the one we set above.**/
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(aprilfools, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
    }
    else
    {/**Load this logo when the date on the players computer is the same as the one we set above.**/
      float sz = .55f;
      if (!DebugOptions.PRINT_BALANCE) uiDrawer.draw(logo, sz, sz, sz/2, sz/2, uiDrawer.r/2, sz/2, 0, ManiColor.W);
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
