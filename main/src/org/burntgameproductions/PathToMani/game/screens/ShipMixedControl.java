

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.GameOptions;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.input.Mover;
import org.burntgameproductions.PathToMani.ManiApplication;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.input.Shooter;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.ManiInputManager;
import org.burntgameproductions.PathToMani.ui.ManiUiControl;

import java.util.List;

public class ShipMixedControl implements ShipUiControl {
  public final ManiUiControl upCtrl;
  private final ManiUiControl myDownCtrl;
  private final Vector2 myMouseWorldPos;
  private final TextureAtlas.AtlasRegion myCursor;
  public final ManiUiControl shootCtrl;
  public final ManiUiControl shoot2Ctrl;
  public final ManiUiControl abilityCtrl;

  private boolean myRight;
  private boolean myLeft;

  public ShipMixedControl(ManiApplication cmp, List<ManiUiControl> controls) {
    GameOptions gameOptions = cmp.getOptions();
    myCursor = cmp.getTexMan().getTex("ui/cursorTarget", null);
    myMouseWorldPos = new Vector2();
    upCtrl = new ManiUiControl(null, false, gameOptions.getKeyUpMouse());
    controls.add(upCtrl);
    myDownCtrl = new ManiUiControl(null, false, gameOptions.getKeyDownMouse());
    controls.add(myDownCtrl);
    shootCtrl = new ManiUiControl(null, false, gameOptions.getKeyShoot());
    controls.add(shootCtrl);
    shoot2Ctrl = new ManiUiControl(null, false, gameOptions.getKeyShoot2());
    controls.add(shoot2Ctrl);
    abilityCtrl = new ManiUiControl(null, false, gameOptions.getKeyAbility());
    controls.add(abilityCtrl);
  }

  @Override
  public void update(ManiApplication cmp, boolean enabled) {
    GameOptions gameOptions = cmp.getOptions();
    blur();
    if (!enabled) return;
    ManiInputManager im = cmp.getInputMan();
    ManiGame g = cmp.getGame();
    ManiShip h = g.getHero();
    if (h != null) {
      myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
      g.getCam().screenToWorld(myMouseWorldPos);
      float desiredAngle = ManiMath.angle(h.getPosition(), myMouseWorldPos);
      Boolean ntt = Mover.needsToTurn(h.getAngle(), desiredAngle, h.getRotSpd(), h.getRotAcc(), Shooter.MIN_SHOOT_AAD);
      if (ntt != null) {
        if (ntt) myRight = true; else myLeft = true;
      }
      if (!im.isMouseOnUi()) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) shootCtrl.maybeFlashPressed(gameOptions.getKeyShoot());
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) shoot2Ctrl.maybeFlashPressed(gameOptions.getKeyShoot2());
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) abilityCtrl.maybeFlashPressed(gameOptions.getKeyAbility());
      }
    }
  }

  @Override
  public boolean isLeft() {
    return myLeft;
  }

  @Override
  public boolean isRight() {
    return myRight;
  }

  @Override
  public boolean isUp() {
    return upCtrl.isOn();
  }

  @Override
  public boolean isDown() {
    return myDownCtrl.isOn();
  }

  @Override
  public boolean isShoot() {
    return shootCtrl.isOn();
  }

  @Override
  public boolean isShoot2() {
    return shoot2Ctrl.isOn();
  }

  @Override
  public boolean isAbility() {
    return abilityCtrl.isOn();
  }

  @Override
  public TextureAtlas.AtlasRegion getInGameTex() {
    return myCursor;
  }

  @Override
  public void blur() {
    myLeft = false;
    myRight = false;
  }
}
