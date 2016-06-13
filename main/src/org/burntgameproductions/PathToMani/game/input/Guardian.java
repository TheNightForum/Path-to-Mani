

package org.burntgameproductions.PathToMani.game.input;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.ObjectManager;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.ship.FarShip;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

import java.util.List;

/**
 * Flies near the given ship. When the ship is destroyed, floats
 */
public class Guardian implements MoveDestProvider {
  public static final float DIST = 1.5f;

  private final Pilot myTargetPilot;
  private final Vector2 myDest;
  private final float myRelAngle;

  private ManiShip myTarget;
  private FarShip myFarTarget;

  public Guardian(ManiGame game, HullConfig hullConfig, Pilot targetPilot, Vector2 targetPos, HullConfig targetHc,
                  float relAngle)
  {
    myTargetPilot = targetPilot;
    myDest = new Vector2();
    myRelAngle = relAngle;
    setDest(game, targetPos, targetHc.getApproxRadius(), hullConfig);
  }

  @Override
  public Vector2 getDest() {
    return myDest;
  }

  @Override
  public boolean shouldAvoidBigObjs() {
    return false;
  }

  @Override
  public float getDesiredSpdLen() {
    return Const.MAX_MOVE_SPD;
  }

  @Override
  public boolean shouldStopNearDest() {
    return true;
  }

  @Override
  public void update(ManiGame game, Vector2 shipPos, float maxIdleDist, HullConfig hullConfig, ManiShip nearestEnemy) {
    updateTarget(game);
    myDest.set(shipPos);
    Vector2 targetPos;
    float targetApproxRad;
    if (myTarget == null) {
      if (myFarTarget == null) return;
      targetPos = myFarTarget.getPos();
      targetApproxRad = myFarTarget.getHullConfig().getApproxRadius();
    } else {
      targetPos = myTarget.getPosition();
      targetApproxRad = myTarget.getHull().config.getApproxRadius();
    }
    setDest(game, targetPos, targetApproxRad, hullConfig);
  }

  public void updateTarget(ManiGame game) {
    ObjectManager om = game.getObjMan();
    List<ManiObject> objs = om.getObjs();
    if (myTarget != null && objs.contains(myTarget)) return;
    myTarget = null;
    List<FarShip> farShips = om.getFarShips();
    if (myFarTarget != null && farShips.contains(myFarTarget)) return;
    myFarTarget = null;

    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      ManiObject o = objs.get(i);
      if (!(o instanceof ManiShip)) continue;
      ManiShip other = (ManiShip) o;
      if (other.getPilot() != myTargetPilot) continue;
      myTarget = other;
      return;
    }
    for (int i = 0, farObjsSize = farShips.size(); i < farObjsSize; i++) {
      FarShip other = farShips.get(i);
      if (other.getPilot() != myTargetPilot) continue;
      myFarTarget = other;
      return;
    }
  }

  private void setDest(ManiGame game, Vector2 targetPos, float targetApproxRad, HullConfig hullConfig) {
    Planet np = game.getPlanetMan().getNearestPlanet(targetPos);
    float desiredAngle = myRelAngle;
    if (np.isNearGround(targetPos)) {
      desiredAngle = SolMath.angle(np.getPos(), targetPos);
    }
    SolMath.fromAl(myDest, desiredAngle, targetApproxRad + DIST + hullConfig.getApproxRadius());
    myDest.add(targetPos);
  }

  @Override
  public Boolean shouldManeuver(boolean canShoot, ManiShip nearestEnemy, boolean nearGround) {
    if (!canShoot) return null;
    Vector2 targetPos = null;
    if (myTarget != null) {
      targetPos = myTarget.getPosition();
    } else if (myFarTarget != null) {
      targetPos = myFarTarget.getPos();
    }
    float maxManeuverDist = 2 * (nearGround ? Const.CAM_VIEW_DIST_GROUND : Const.CAM_VIEW_DIST_SPACE);
    if (targetPos != null && maxManeuverDist < targetPos.dst(nearestEnemy.getPosition())) return null;
    return true;
  }

  @Override
  public Vector2 getDestSpd() {
    return myTarget == null ? Vector2.Zero : myTarget.getSpd();
  }

  public float getRelAngle() {
    return myRelAngle;
  }
}
