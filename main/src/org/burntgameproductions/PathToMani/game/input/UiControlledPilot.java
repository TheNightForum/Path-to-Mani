

package org.burntgameproductions.PathToMani.game.input;


import org.burntgameproductions.PathToMani.game.Faction;
import org.burntgameproductions.PathToMani.game.screens.MainScreen;
import org.burntgameproductions.PathToMani.game.ship.FarShip;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

public class UiControlledPilot implements Pilot {

  private final MainScreen myScreen;

  public UiControlledPilot(MainScreen screen) {
    myScreen = screen;
  }

  @Override
  public void update(ManiGame game, ManiShip ship, ManiShip nearestEnemy) {
  }

  @Override
  public boolean isUp() {
    return myScreen.isUp();
  }

  @Override
  public boolean isLeft() {
    return myScreen.isLeft();
  }

  @Override
  public boolean isRight() {
    return myScreen.isRight();
  }

  @Override
  public boolean isShoot() {
    return myScreen.isShoot();
  }

  @Override
  public boolean isShoot2() {
    return myScreen.isShoot2();
  }

  @Override
  public boolean collectsItems() {
    return true;
  }

  @Override
  public boolean isAbility() {
    return myScreen.isAbility();
  }

  @Override
  public Faction getFaction() {
    return Faction.LAANI;
  }

  @Override
  public boolean shootsAtObstacles() {
    return false;
  }

  @Override
  public float getDetectionDist() {
    return Const.AUTO_SHOOT_SPACE; // just for unfixed mounts
  }

  @Override
  public String getMapHint() {
    return "You";
  }

  @Override
  public void updateFar(ManiGame game, FarShip farShip) {
  }

  @Override
  public String toDebugString() {
    return "";
  }

  @Override
  public boolean isPlayer() {
    return true;
  }
}
