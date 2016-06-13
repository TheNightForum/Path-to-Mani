

package org.burntgameproductions.PathToMani;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import org.burntgameproductions.PathToMani.common.SolColor;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.DebugOptions;
import org.burntgameproductions.PathToMani.game.sound.MusicManager;
import org.burntgameproductions.PathToMani.menu.MenuScreens;
import org.burntgameproductions.PathToMani.ui.DebugCollector;
import org.burntgameproductions.PathToMani.ui.FontSize;
import org.burntgameproductions.PathToMani.ui.ManiLayouts;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ManiApplication implements ApplicationListener {

  private ManiInputManager myInputMan;
  private UiDrawer myUiDrawer;
  private MenuScreens myMenuScreens;
  private TextureManager myTextureManager;
  private ManiLayouts myLayouts;
  private boolean myReallyMobile;
  private GameOptions myOptions;
  private CommonDrawer myCommonDrawer;
  private FPSLogger  myFpsLogger;

  private String myFatalErrorMsg;
  private String myFatalErrorTrace;

  private float myAccum = 0;
  private ManiGame myGame;

  public ManiApplication() {
    // Initiate Box2D to make sure natives are loaded early enough
    Box2D.init();
  }

  @Override
  public void create() {

    myReallyMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
    if (myReallyMobile) DebugOptions.read(null);
    myOptions = new GameOptions(isMobile(), null);

    MusicManager.getInstance().PlayMenuMusic(myOptions);

    myTextureManager = new TextureManager();
    myCommonDrawer = new CommonDrawer();
    myUiDrawer = new UiDrawer(myTextureManager, myCommonDrawer);
    myInputMan = new ManiInputManager(myTextureManager, myUiDrawer.r);
    myLayouts = new ManiLayouts(myUiDrawer.r);
    myMenuScreens = new MenuScreens(myLayouts, myTextureManager, isMobile(), myUiDrawer.r, myOptions);

    myInputMan.setScreen(this, myMenuScreens.main);
    myFpsLogger = new FPSLogger();
  }

  @Override
  public void resize(int i, int i1) {

  }

  public void render() {
    myAccum += Gdx.graphics.getDeltaTime();
    while (myAccum > Const.REAL_TIME_STEP) {
      safeUpdate();
      myAccum -= Const.REAL_TIME_STEP;

    }
    draw();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  private void safeUpdate() {
    if (myFatalErrorMsg != null) return;
    try {
      update();
    } catch (Throwable t) {
      t.printStackTrace();
      myFatalErrorMsg = "A fatal error occurred:\n" + t.getMessage();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      myFatalErrorTrace = sw.toString();

      if (!myReallyMobile) {
        throw t;
      }
    }
  }

  private void update() {
    DebugCollector.update();
    if (DebugOptions.SHOW_FPS) {
      DebugCollector.debug("Fps", Gdx.graphics.getFramesPerSecond());
      myFpsLogger.log();
    }
    myInputMan.update(this);
    if (myGame != null) {
      myGame.update();
    }

    SolMath.checkVectorsTaken(null);
  }

  private void draw() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    myCommonDrawer.begin();
    if (myGame != null) {
      myGame.draw();
    }
    myUiDrawer.updateMtx();
    myInputMan.draw(myUiDrawer, this);
    if (myGame != null) {
      myGame.drawDebugUi(myUiDrawer);
    }
    if (myFatalErrorMsg != null) {
      myUiDrawer.draw(myUiDrawer.whiteTex, myUiDrawer.r, .5f, 0, 0, 0, .25f, 0, SolColor.UI_BG);
      myUiDrawer.drawString(myFatalErrorMsg, myUiDrawer.r / 2, .5f, FontSize.MENU, true, SolColor.W);
      myUiDrawer.drawString(myFatalErrorTrace, .2f * myUiDrawer.r, .6f, FontSize.DEBUG, false, SolColor.W);
    }
    DebugCollector.draw(myUiDrawer);
    if (myGame == null) {
      myUiDrawer.drawString("version: " + Const.VERSION, 0.01f, .98f, FontSize.DEBUG, false, SolColor.W);
    }
    myCommonDrawer.end();
  }

  public void loadNewGame(boolean tut, boolean usePrevShip) {
    if (myGame != null) throw new AssertionError("Starting a new game with unfinished current one");
    myInputMan.setScreen(this, myMenuScreens.loading);
    myMenuScreens.loading.setMode(tut, usePrevShip);
    MusicManager.getInstance().PlayGameMusic(myOptions);
  }

  public void startNewGame(boolean tut, boolean usePrevShip) {
    myGame = new ManiGame(this, usePrevShip, myTextureManager, tut, myCommonDrawer);
    myInputMan.setScreen(this, myGame.getScreens().mainScreen);
    MusicManager.getInstance().PlayGameMusic(myOptions);
  }

  public ManiInputManager getInputMan() {
    return myInputMan;
  }

  public MenuScreens getMenuScreens() {
    return myMenuScreens;
  }

  public void dispose() {
    myCommonDrawer.dispose();
    if (myGame != null) myGame.onGameEnd();
    myTextureManager.dispose();
    myInputMan.dispose();
  }

  public ManiGame getGame() {
    return myGame;
  }

  public ManiLayouts getLayouts() {
    return myLayouts;
  }

  public void finishGame() {
    myGame.onGameEnd();
    myGame = null;
    myInputMan.setScreen(this, myMenuScreens.main);
  }

  public TextureManager getTexMan() {
    return myTextureManager;
  }

  public boolean isMobile() {
    return DebugOptions.EMULATE_MOBILE || myReallyMobile;
  }

  public GameOptions getOptions() {
    return myOptions;
  }

  public void paused() {
    if (myGame != null) myGame.saveShip();
  }
}
