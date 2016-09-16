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

package com.pathtomani.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.pathtomani.game.dra.Dra;
import com.pathtomani.Const;
import com.pathtomani.common.Bound;
import com.pathtomani.common.ManiColor;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.dra.DraLevel;
import com.pathtomani.game.dra.RectSprite;
import com.pathtomani.game.particle.EffectConfig;
import com.pathtomani.game.particle.LightSrc;
import com.pathtomani.game.particle.ParticleSrc;
import com.pathtomani.game.planet.Planet;
import com.pathtomani.game.ship.FarShip;
import com.pathtomani.game.ship.ForceBeacon;
import com.pathtomani.game.ship.ManiShip;
import com.pathtomani.game.ship.Teleport;

import java.util.ArrayList;
import java.util.List;

public class StarPort implements ManiObject {

  public static final float DIST_FROM_PLANET = Const.PLANET_GAP * .5f;
  public static final int SIZE = 8;
  public static final float FARE = 10f;
  private final Body myBody;
  private final ArrayList<LightSrc> myLights;
  private final Vector2 myPos;
  private final Planet myFrom;
  private final Planet myTo;
  private final ArrayList<Dra> myDras;
  private float myAngle;
  private final boolean mySecondary;

  public StarPort(Planet from, Planet to, Body body, ArrayList<Dra> dras, boolean secondary, ArrayList<LightSrc> lights) {
    myFrom = from;
    myTo = to;
    myDras = dras;
    myBody = body;
    myLights = lights;
    myPos = new Vector2();
    setParamsFromBody();
    mySecondary = secondary;
  }

  @Override
  public void update(ManiGame game) {
    setParamsFromBody();

    float fps = 1 / game.getTimeStep();

    Vector2 spd = getDesiredPos(myFrom, myTo, true);
    // Adjust position so that StarPorts are not overlapping
    spd = adjustDesiredPos(game, this, spd);
    spd.sub(myPos).scl(fps/4);
    myBody.setLinearVelocity(spd);
    ManiMath.free(spd);
    float desiredAngle = ManiMath.angle(myFrom.getPos(), myTo.getPos());
    myBody.setAngularVelocity((desiredAngle - myAngle) * ManiMath.degRad * fps/4);

    ManiShip ship = ForceBeacon.pullShips(game, this, myPos, null, null, .4f * SIZE);
    if (ship != null && ship.getMoney() >= FARE && ship.getPosition().dst(myPos) < .05f * SIZE) {
      ship.setMoney(ship.getMoney() - FARE);
      Transcendent t = new Transcendent(ship, myFrom, myTo, game);
      ObjectManager objectManager = game.getObjMan();
      objectManager.addObjDelayed(t);
      blip(game, ship);
      game.getSoundMan().play(game, game.getSpecialSounds().transcendentCreated, null, t);
      objectManager.removeObjDelayed(ship);
    }
    for (int i = 0, myLightsSize = myLights.size(); i < myLightsSize; i++) {
      LightSrc l = myLights.get(i);
      l.update(true, myAngle, game);
    }

  }

  private static void blip(ManiGame game, ManiShip ship) {
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex(Teleport.TEX_PATH, null);
    float blipSz = ship.getHull().config.getApproxRadius() * 10;
    game.getPartMan().blip(game, ship.getPosition(), ManiMath.rnd(180), blipSz, 1, Vector2.Zero, tex);
  }

  public boolean isSecondary() {
    return mySecondary;
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return false;
  }

  @Override
  public void onRemove(ManiGame game) {
    myBody.getWorld().destroyBody(myBody);

  }

  @Override
  public void receiveDmg(float dmg, ManiGame game, Vector2 pos, DmgType dmgType) {
    game.getSpecialSounds().playHit(game, this, pos, dmgType);
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveForce(Vector2 force, ManiGame game, boolean acc) {

  }

  @Override
  public Vector2 getPosition() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return new MyFar(myFrom, myTo, myPos, mySecondary);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public float getAngle() {
    return myAngle;
  }

  @Override
  public Vector2 getSpd() {
    return null;
  }

  @Override
  public void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                            ManiGame game, Vector2 collPos)
  {

  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return true;
  }

  @Override
  public boolean hasBody() {
    return true;
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * ManiMath.radDeg;
  }

  @Bound
  public static Vector2 getDesiredPos(Planet from, Planet to, boolean percise) {
    Vector2 fromPos = from.getPos();
    float angle = ManiMath.angle(fromPos, to.getPos(), percise);
    Vector2 pos = ManiMath.getVec();
    ManiMath.fromAl(pos, angle, from.getFullHeight() + DIST_FROM_PLANET);
    pos.add(fromPos);
    return pos;
  }

  private static Vector2 adjustDesiredPos(ManiGame game, StarPort myPort, Vector2 desired) {
    Vector2 newPos = desired;
    List<ManiObject> objs = game.getObjMan().getObjs();
    for (ManiObject o : objs) {
      if (o instanceof StarPort && o != myPort) {
        StarPort sp = (StarPort)o;
        // Check if the positions overlap
        Vector2 fromPos = sp.getPosition();
        Vector2 distVec = ManiMath.distVec(fromPos, desired);
        float distance = ManiMath.hypotenuse(distVec.x, distVec.y);
        if (distance <= (float)StarPort.SIZE) {
          distVec.scl((StarPort.SIZE + .5f) / distance);
          newPos = fromPos.cpy().add(distVec);
          Vector2 d2 = ManiMath.distVec(fromPos, newPos);
          ManiMath.free(d2);
        }
        ManiMath.free(distVec);
      }
    }
    return newPos;
  }

  public Planet getFrom() {
    return myFrom;
  }

  public Planet getTo() {
    return myTo;
  }

  public static class Builder {
    public static final float FLOW_DIST = .26f * SIZE;
    private final PathLoader myLoader;

    public Builder() {
      myLoader = new PathLoader("misc");
    }

    public StarPort build(ManiGame game, Planet from, Planet to, boolean secondary) {
      float angle = ManiMath.angle(from.getPos(), to.getPos());
      Vector2 pos = getDesiredPos(from, to, false);
      // Adjust position so that StarPorts are not overlapping
      pos = adjustDesiredPos(game, null, pos);
      ArrayList<Dra> dras = new ArrayList<Dra>();
      Body body = myLoader.getBodyAndSprite(game, "smallGameObjs", "starPort", SIZE,
        BodyDef.BodyType.KinematicBody, new Vector2(pos), angle, dras, 10f, DraLevel.BIG_BODIES, null);
      ManiMath.free(pos);
      ArrayList<LightSrc> lights = new ArrayList<LightSrc>();
      addFlow(game, pos, dras, 0, lights);
      addFlow(game, pos, dras, 90, lights);
      addFlow(game, pos, dras, -90, lights);
      addFlow(game, pos, dras, 180, lights);
      ParticleSrc force = game.getSpecialEffects().buildForceBeacon(FLOW_DIST * 1.5f, game, new Vector2(), pos, Vector2.Zero);
      force.setWorking(true);
      dras.add(force);
      StarPort sp = new StarPort(from, to, body, dras, secondary, lights);
      body.setUserData(sp);
      return sp;
    }
    private void addFlow(ManiGame game, Vector2 pos, ArrayList<Dra> dras, float angle, ArrayList<LightSrc> lights) {
      EffectConfig flow = game.getSpecialEffects().starPortFlow;
      Vector2 relPos = new Vector2();
      ManiMath.fromAl(relPos, angle, -FLOW_DIST);
      ParticleSrc f1 = new ParticleSrc(flow, FLOW_DIST, DraLevel.PART_BG_0, relPos, false, game, pos, Vector2.Zero, angle);
      f1.setWorking(true);
      dras.add(f1);
      LightSrc light = new LightSrc(game, .6f, true, 1, relPos, flow.tint);
      light.collectDras(dras);
      lights.add(light);
    }
  }

  public static class MyFar implements FarObj {
    private final Planet myFrom;
    private final Planet myTo;
    private final Vector2 myPos;
    private final boolean mySecondary;
    private float myAngle;

    public MyFar(Planet from, Planet to, Vector2 pos, boolean secondary) {
      myFrom = from;
      myTo = to;
      myPos = new Vector2(pos);
      mySecondary = secondary;
    }

    @Override
    public boolean shouldBeRemoved(ManiGame game) {
      return false;
    }

    @Override
    public ManiObject toObj(ManiGame game) {
      return game.getStarPortBuilder().build(game, myFrom, myTo, mySecondary);
    }

    @Override
    public void update(ManiGame game) {

      Vector2 dp = getDesiredPos(myFrom, myTo, false);
      myPos.set(dp);
      ManiMath.free(dp);
      myAngle = ManiMath.angle(myFrom.getPos(), myTo.getPos());
    }

    @Override
    public float getRadius() {
      return SIZE/2;
    }

    @Override
    public Vector2 getPos() {
      return myPos;
    }

    @Override
    public String toDebugString() {
      return null;
    }

    @Override
    public boolean hasBody() {
      return true;
    }

    public Planet getFrom() {
      return myFrom;
    }

    public Planet getTo() {
      return myTo;
    }

    public float getAngle() {
      return myAngle;
    }

    public boolean isSecondary() {
      return mySecondary;
    }
  }

  public static class Transcendent implements ManiObject {
    private static final float TRAN_SZ = 1f;
    private final Planet myFrom;
    private final Planet myTo;
    private final Vector2 myPos;
    private final Vector2 myDestPos;
    private final ArrayList<Dra> myDras;
    private final FarShip myShip;
    private final Vector2 mySpd;
    private final LightSrc myLight;

    private float myAngle;
    private final ParticleSrc myEff;

    public Transcendent(ManiShip ship, Planet from, Planet to, ManiGame game) {
      myShip = ship.toFarObj();
      myFrom = from;
      myTo = to;
      myPos = new Vector2(ship.getPosition());
      mySpd = new Vector2();
      myDestPos = new Vector2();

      RectSprite s = new RectSprite(game.getTexMan().getTex("smallGameObjs/transcendent", null), TRAN_SZ, .3f, 0, new Vector2(), DraLevel.PROJECTILES, 0, 0, ManiColor.W, false);
      myDras = new ArrayList<Dra>();
      myDras.add(s);
      EffectConfig eff = game.getSpecialEffects().transcendentWork;
      myEff = new ParticleSrc(eff, TRAN_SZ, DraLevel.PART_BG_0, new Vector2(), true, game, myPos, Vector2.Zero, 0);
      myEff.setWorking(true);
      myDras.add(myEff);
      myLight = new LightSrc(game, .6f * TRAN_SZ, true, .5f, new Vector2(), eff.tint);
      myLight.collectDras(myDras);
      setDependentParams();
    }

    public FarShip getShip() {
      return myShip;
    }

    @Override
    public void update(ManiGame game) {
      setDependentParams();

      float ts = game.getTimeStep();
      Vector2 moveDiff = ManiMath.getVec(mySpd);
      moveDiff.scl(ts);
      myPos.add(moveDiff);
      ManiMath.free(moveDiff);

      if (myPos.dst(myDestPos) < .5f) {
        ObjectManager objectManager = game.getObjMan();
        objectManager.removeObjDelayed(this);
        myShip.setPos(myPos);
        myShip.setSpd(new Vector2());
        ManiShip ship = myShip.toObj(game);
        objectManager.addObjDelayed(ship);
        blip(game, ship);
        game.getSoundMan().play(game, game.getSpecialSounds().transcendentFinished, null, this);
        game.getObjMan().resetDelays(); // because of the hacked speed
      } else {
        game.getSoundMan().play(game, game.getSpecialSounds().transcendentMove, null, this);
        myLight.update(true, myAngle, game);
      }
    }

    private void setDependentParams() {
      Vector2 toPos = myTo.getPos();
      float nodeAngle = ManiMath.angle(toPos, myFrom.getPos());
      ManiMath.fromAl(myDestPos, nodeAngle, myTo.getFullHeight() + DIST_FROM_PLANET + SIZE/2);
      myDestPos.add(toPos);
      myAngle = ManiMath.angle(myPos, myDestPos);
      ManiMath.fromAl(mySpd, myAngle, Const.MAX_MOVE_SPD * 2); //hack again : (
    }

    @Override
    public boolean shouldBeRemoved(ManiGame game) {
      return false;
    }

    @Override
    public void onRemove(ManiGame game) {
      game.getPartMan().finish(game, myEff, myPos);
    }

    @Override
    public void receiveDmg(float dmg, ManiGame game, Vector2 pos, DmgType dmgType) {
      game.getSpecialSounds().playHit(game, this, pos, dmgType);
    }

    @Override
    public boolean receivesGravity() {
      return false;
    }

    @Override
    public void receiveForce(Vector2 force, ManiGame game, boolean acc) {
    }

    @Override
    public Vector2 getPosition() {
      return myPos;
    }

    @Override
    public FarObj toFarObj() {
      return null;
    }

    @Override
    public List<Dra> getDras() {
      return myDras;
    }

    @Override
    public float getAngle() {
      return myAngle;
    }

    @Override
    public Vector2 getSpd() {
      return mySpd;
    }

    @Override
    public void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              ManiGame game, Vector2 collPos)
    {
    }

    @Override
    public String toDebugString() {
      return null;
    }

    @Override
    public Boolean isMetal() {
      return null;
    }

    @Override
    public boolean hasBody() {
      return false;
    }
  }
}
