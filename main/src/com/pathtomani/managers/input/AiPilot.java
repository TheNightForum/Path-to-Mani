/*
 * Copyright 2016 BurntGameProductions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pathtomani.managers.input;

import com.badlogic.gdx.math.Vector2;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.ManiGame;
import com.pathtomani.entities.gun.GunItem;
import com.pathtomani.entities.item.EngineItem;
import com.pathtomani.entities.planet.Planet;
import com.pathtomani.entities.planet.PlanetBind;
import com.pathtomani.entities.ship.FarShip;
import com.pathtomani.entities.ship.hulls.HullConfig;
import com.pathtomani.game.Faction;
import com.pathtomani.entities.ship.ManiShip;

public class AiPilot implements Pilot {

  public static final float MIN_IDLE_DIST = .8f;
  public static final float MAX_GROUND_BATTLE_SPD = .7f;
  public static final float MAX_BATTLE_SPD_BIG = 1f;
  public static final float MAX_BATTLE_SPD = 2f;
  public static final float MAX_BIND_AWAIT = .25f;
  public static final float MAX_RE_EQUIP_AWAIT = 3f;

  private final MoveDestProvider myDestProvider;
  private final boolean myCollectsItems;
  private final Mover myMover;
  private final Shooter myShooter;
  private final Faction myFaction;
  private final boolean myShootAtObstacles;
  private final String myMapHint;
  private final BattleDestProvider myBattleDestProvider;
  private final float myDetectionDist;
  private final AbilityUpdater myAbilityUpdater;

  private float myBindAwait;
  private PlanetBind myPlanetBind;
  private float myReEquipAwait;

  public AiPilot(MoveDestProvider destProvider, boolean collectsItems, Faction faction,
    boolean shootAtObstacles, String mapHint, float detectionDist)
  {
    myDestProvider = destProvider;
    myDetectionDist = detectionDist;
    myMover = new Mover();
    myShooter = new Shooter();
    myBattleDestProvider = new BattleDestProvider();
    myCollectsItems = collectsItems;
    myFaction = faction;
    myShootAtObstacles = shootAtObstacles;
    myMapHint = mapHint;
    myAbilityUpdater = new AbilityUpdater();
  }

  @Override
  public void update(ManiGame game, ManiShip ship, ManiShip nearestEnemy) {
    myAbilityUpdater.update(ship, nearestEnemy);
    myPlanetBind = null;
    Vector2 shipPos = ship.getPosition();
    HullConfig hullConfig = ship.getHull().config;
    float maxIdleDist = getMaxIdleDist(hullConfig);
    myDestProvider.update(game, shipPos, maxIdleDist, hullConfig, nearestEnemy);

    Boolean canShoot = canShoot0(ship);
    boolean canShootUnfixed = canShoot == null;
    if (canShootUnfixed) canShoot = true;
    Planet np = game.getPlanetMan().getNearestPlanet();
    boolean nearGround = np.isNearGround(shipPos);

    Vector2 dest = null;
    Vector2 destSpd = null;
    boolean shouldStopNearDest = false;
    boolean avoidBigObjs = false;
    float desiredSpdLen = myDestProvider.getDesiredSpdLen();
    boolean hasEngine = ship.getHull().getEngine() != null;
    if (hasEngine) {
      Boolean battle = null;
      if (nearestEnemy != null) battle = myDestProvider.shouldManeuver(canShoot, nearestEnemy, nearGround);
      if (battle != null) {
        dest = myBattleDestProvider.getDest(ship, nearestEnemy, np, battle, game.getTimeStep(), canShootUnfixed, nearGround);
        shouldStopNearDest = myBattleDestProvider.shouldStopNearDest();
        destSpd = nearestEnemy.getSpd();
        boolean big = hullConfig.getType() == HullConfig.Type.BIG;
        float maxBattleSpd = nearGround ? MAX_GROUND_BATTLE_SPD : big ? MAX_BATTLE_SPD_BIG : MAX_BATTLE_SPD;
        if (maxBattleSpd < desiredSpdLen) desiredSpdLen = maxBattleSpd;
        if (!big) desiredSpdLen += destSpd.len();
      } else {
        dest = myDestProvider.getDest();
        destSpd = myDestProvider.getDestSpd();
        shouldStopNearDest = myDestProvider.shouldStopNearDest();
        avoidBigObjs = myDestProvider.shouldAvoidBigObjs();
      }
    }

    myMover.update(game, ship, dest, np, maxIdleDist, hasEngine, avoidBigObjs, desiredSpdLen, shouldStopNearDest, destSpd);
    boolean moverActive = myMover.isActive();

    Vector2 enemyPos = nearestEnemy == null ? null : nearestEnemy.getPosition();
    Vector2 enemySpd = nearestEnemy == null ? null : nearestEnemy.getSpd();
    float enemyApproxRad = nearestEnemy == null ? 0 : nearestEnemy.getHull().config.getApproxRadius();
    myShooter.update(ship, enemyPos, moverActive, canShoot, enemySpd, enemyApproxRad);
    if (hasEngine && !moverActive && !isShooterRotated()) {
      myMover.rotateOnIdle(ship, np, dest, shouldStopNearDest, maxIdleDist);
    }

    if (myReEquipAwait <= 0) {
      myReEquipAwait = MAX_RE_EQUIP_AWAIT;
    } else {
      myReEquipAwait -= game.getTimeStep();
    }
  }

  private float getMaxIdleDist(HullConfig hullConfig) {
    float maxIdleDist = hullConfig.getApproxRadius();
    if (maxIdleDist < MIN_IDLE_DIST) maxIdleDist = MIN_IDLE_DIST;
    return maxIdleDist;
  }

  private Boolean canShoot0(ManiShip ship) {
    GunItem g1 = ship.getHull().getGun(false);
    if (g1 != null && g1.canShoot()) return !g1.config.fixed ? null : true;
    GunItem g2 = ship.getHull().getGun(true);
    if (g2 != null && (g2.canShoot())) return !g2.config.fixed ? null : true;
    return false;
  }

  private boolean isShooterRotated() {
    return myShooter.isLeft() || myShooter.isRight();
  }

  @Override
  public boolean isUp() {
    return myMover.isUp();
  }

  @Override
  public boolean isLeft() {
    return myMover.isLeft() || myShooter.isLeft();
  }

  @Override
  public boolean isRight() {
    return myMover.isRight() || myShooter.isRight();
  }

  @Override
  public boolean isShoot() {
    return myShooter.isShoot();
  }

  @Override
  public boolean isShoot2() {
    return myShooter.isShoot2();
  }

  @Override
  public boolean collectsItems() {
    return myCollectsItems;
  }

  @Override
  public boolean isAbility() {
    return myAbilityUpdater.isAbility();
  }

  @Override
  public Faction getFaction() {
    return myFaction;
  }

  @Override
  public boolean shootsAtObstacles() {
    return myShootAtObstacles;
  }

  @Override
  public float getDetectionDist() {
    return myDetectionDist;
  }

  @Override
  public String getMapHint() {
    return myMapHint;
  }

  @Override
  public void updateFar(ManiGame game, FarShip farShip) {
    Vector2 shipPos = farShip.getPos();
    HullConfig hullConfig = farShip.getHullConfig();
    float maxIdleDist = getMaxIdleDist(hullConfig);
    myDestProvider.update(game, shipPos, maxIdleDist, hullConfig, null);
    Vector2 dest = myDestProvider.getDest();

    Vector2 spd = farShip.getSpd();
    float angle = farShip.getAngle();
    EngineItem engine = farShip.getEngine();
    float ts = game.getTimeStep();
    if (dest == null || engine == null) {
      if (myPlanetBind == null) {
        if (myBindAwait > 0) {
          myBindAwait -= ts;
        } else {
          myPlanetBind = PlanetBind.tryBind(game, shipPos, angle);
          myBindAwait = MAX_BIND_AWAIT;
        }
      }
      if (myPlanetBind != null) {
        myPlanetBind.setDiff(spd, shipPos, false);
        spd.scl(1/ ts);
        angle = myPlanetBind.getDesiredAngle();
      }
    } else {
      float toDestLen = shipPos.dst(dest);
      float desiredAngle;
      float maxIdleDistHack = .05f; // to avoid StillGuards from getting stuck inside ground
      if (myDestProvider.shouldStopNearDest() && toDestLen < maxIdleDistHack) {
        spd.set(myDestProvider.getDestSpd());
        desiredAngle = angle; // can be improved
      } else {
        desiredAngle = ManiMath.angle(shipPos, dest);
        if (myDestProvider.shouldAvoidBigObjs()) {
          desiredAngle = myMover.getBigObjAvoider().avoid(game, shipPos, dest, desiredAngle);
        }
        float desiredSpdLen = myDestProvider.getDesiredSpdLen();
        float spdLenDiff = engine.getAcc() * ts;
        float spdLen = ManiMath.approach(spd.len(), desiredSpdLen, spdLenDiff);
        if (toDestLen < spdLen) spdLen = toDestLen;
        ManiMath.fromAl(spd, desiredAngle, spdLen);
      }
      angle = ManiMath.approachAngle(angle, desiredAngle, engine.getMaxRotSpd() * ts);
    }

    farShip.setSpd(spd);
    farShip.setAngle(angle);

    Vector2 newPos = ManiMath.getVec(spd);
    newPos.scl(ts);
    newPos.add(shipPos);
    farShip.setPos(newPos);
    ManiMath.free(newPos);
  }

  @Override
  public String toDebugString() {
    return "moverActive: " + myMover.isActive();
  }

  @Override
  public boolean isPlayer() {
    return myDestProvider instanceof BeaconDestProvider;
  }

}
