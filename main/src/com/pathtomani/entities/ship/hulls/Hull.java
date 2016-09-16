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

package com.pathtomani.entities.ship.hulls;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.Faction;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.dra.Dra;
import com.pathtomani.entities.gun.GunItem;
import com.pathtomani.entities.gun.GunMount;
import com.pathtomani.game.input.Pilot;
import com.pathtomani.entities.item.EngineItem;
import com.pathtomani.entities.item.ItemContainer;
import com.pathtomani.effects.particle.LightSrc;
import com.pathtomani.entities.planet.PlanetBind;
import com.pathtomani.entities.ship.Door;
import com.pathtomani.entities.ship.ManiShip;
import com.pathtomani.entities.ship.ShipEngine;
import com.pathtomani.entities.ship.ForceBeacon;

import java.util.ArrayList;
import java.util.List;

public class Hull {

  public final HullConfig config;
  private final Body myBody;
  private final GunMount myGunMount1;
  private final GunMount myGunMount2;
  private final Fixture myBase;
  private final List<LightSrc> myLightSrcs;
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final ArrayList<ForceBeacon> myBeacons;
  private final PlanetBind myPlanetBind;
  private final float myMass;

  public float life;
  private final ArrayList<Door> myDoors;
  private final Fixture myShieldFixture;
  private float myAngle;
  private float myRotSpd;
  private ShipEngine myEngine;

  public Hull(ManiGame game, HullConfig hullConfig, Body body, GunMount gunMount1, GunMount gunMount2, Fixture base,
              List<LightSrc> lightSrcs, float life, ArrayList<ForceBeacon> forceBeacons,
              ArrayList<Door> doors, Fixture shieldFixture)
  {
    config = hullConfig;
    myBody = body;
    myGunMount1 = gunMount1;
    myGunMount2 = gunMount2;
    myBase = base;
    myLightSrcs = lightSrcs;
    this.life = life;
    myDoors = doors;
    myShieldFixture = shieldFixture;
    myPos = new Vector2();
    mySpd = new Vector2();
    myBeacons = forceBeacons;

    myMass = myBody.getMass();
    setParamsFromBody();

    myPlanetBind = config.getType() == HullConfig.Type.STATION ? PlanetBind.tryBind(game, myPos, myAngle) : null;

  }

  public Body getBody() {
    return myBody;
  }

  public Fixture getBase() {
    return myBase;
  }

  public GunMount getGunMount(boolean second) {
    return second ? myGunMount2 : myGunMount1;
  }

  public GunItem getGun(boolean second) {
    GunMount m = getGunMount(second);
    if (m == null) return null;
    return m.getGun();
  }

  public void update(ManiGame game, ItemContainer container, Pilot provider, ManiShip ship, ManiShip nearestEnemy) {
    setParamsFromBody();
    boolean controlsEnabled = ship.isControlsEnabled();

    if (myEngine != null) {
      if (true || container.contains(myEngine.getItem())) {
        myEngine.update(myAngle, game, provider, myBody, mySpd, ship, controlsEnabled, myMass);
      } else {
        setEngine(game, ship, null);
      }
    }

    Faction faction = ship.getPilot().getFaction();
    myGunMount1.update(container, game, myAngle, ship, controlsEnabled && provider.isShoot(), nearestEnemy, faction);
    if (myGunMount2 != null) myGunMount2.update(container, game, myAngle, ship, controlsEnabled && provider.isShoot2(), nearestEnemy, faction);

    for (int i = 0, myLightSrcsSize = myLightSrcs.size(); i < myLightSrcsSize; i++) {
      LightSrc src = myLightSrcs.get(i);
      src.update(true, myAngle, game);
    }

    for (int i = 0, myBeaconsSize = myBeacons.size(); i < myBeaconsSize; i++) {
      ForceBeacon b = myBeacons.get(i);
      b.update(game, myPos, myAngle, ship);
    }

    for (int i = 0, myDoorsSize = myDoors.size(); i < myDoorsSize; i++) {
      Door door = myDoors.get(i);
      door.update(game, ship);
    }

    if (myPlanetBind != null) {
      Vector2 spd = ManiMath.getVec();
      myPlanetBind.setDiff(spd, myPos, true);
      float fps = 1 / game.getTimeStep();
      spd.scl(fps);
      myBody.setLinearVelocity(spd);
      ManiMath.free(spd);
      float angleDiff = myPlanetBind.getDesiredAngle() - myAngle;
      myBody.setAngularVelocity(angleDiff * ManiMath.degRad * fps);
    }
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * ManiMath.radDeg;
    myRotSpd = myBody.getAngularVelocity() * ManiMath.radDeg;
    mySpd.set(myBody.getLinearVelocity());
  }

  public void onRemove(ManiGame game) {
    for (Door door : myDoors) door.onRemove(game);
    myBody.getWorld().destroyBody(myBody);
    if (myEngine != null) myEngine.onRemove(game, myPos);

  }

  public void setEngine(ManiGame game, ManiShip ship, EngineItem ei) {
    List<Dra> dras = ship.getDras();
    if (myEngine != null) {
      List<Dra> dras1 = myEngine.getDras();
      dras.removeAll(dras1);
      game.getDraMan().removeAll(dras1);
      myEngine = null;
    }
    if (ei != null) {
      myEngine = new ShipEngine(game, ei, config.getE1Pos(), config.getE2Pos(), ship);
      List<Dra> dras1 = myEngine.getDras();
      dras.addAll(dras1);
      game.getDraMan().addAll(dras1);
    }
  }

  public float getAngle() {
    return myAngle;
  }

  public Vector2 getPos() {
    return myPos;
  }

  public Vector2 getSpd() {
    return mySpd;
  }

  public EngineItem getEngine() {
    return myEngine == null ? null : myEngine.getItem();
  }

  public float getRotSpd() {
    return myRotSpd;
  }

  public ArrayList<Door> getDoors() {
    return myDoors;
  }

  public Fixture getShieldFixture() {
    return myShieldFixture;
  }

  public float getMass() {
    return myMass;
  }
}
