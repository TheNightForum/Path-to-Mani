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

package com.pathtomani.entities.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.dra.Dra;
import com.pathtomani.entities.item.EngineItem;
import com.pathtomani.effects.particle.LightSrc;
import com.pathtomani.game.sound.ManiSound;
import com.pathtomani.game.ManiObject;
import com.pathtomani.game.dra.DraLevel;
import com.pathtomani.game.input.Pilot;
import com.pathtomani.effects.particle.EffectConfig;
import com.pathtomani.effects.particle.PartMan;
import com.pathtomani.effects.particle.ParticleSrc;

import java.util.ArrayList;
import java.util.List;

public class ShipEngine {
  public static final float MAX_RECOVER_ROT_SPD = 5f;
  public static final float RECOVER_MUL = 15f;
  public static final float RECOVER_AWAIT = 2f;

  private final ParticleSrc myFlameSrc1;
  private final ParticleSrc myFlameSrc2;
  private final LightSrc myLightSrc1;
  private final LightSrc myLightSrc2;
  private final EngineItem myItem;
  private final List<Dra> myDras;
  private float myRecoverAwait;

  public ShipEngine(ManiGame game, EngineItem ei, Vector2 e1RelPos, Vector2 e2RelPos, ManiShip ship) {
    myItem = ei;
    myDras = new ArrayList<Dra>();
    EffectConfig ec = myItem.getEffectConfig();
    Vector2 shipPos = ship.getPosition();
    Vector2 shipSpd = ship.getSpd();
    myFlameSrc1 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e1RelPos, true, game, shipPos, shipSpd, 0);
    myDras.add(myFlameSrc1);
    myFlameSrc2 = new ParticleSrc(ec, -1, DraLevel.PART_BG_0, e2RelPos, true, game, shipPos, shipSpd, 0);
    myDras.add(myFlameSrc2);
    float lightSz = ec.sz * 2.5f;
    myLightSrc1 = new LightSrc(game, lightSz, true, .7f, new Vector2(e1RelPos), ec.tint);
    myLightSrc1.collectDras(myDras);
    myLightSrc2 = new LightSrc(game, lightSz, true, .7f, new Vector2(e2RelPos), ec.tint);
    myLightSrc2.collectDras(myDras);
  }

  public List<Dra> getDras() {
    return myDras;
  }

  public void update(float angle, ManiGame game, Pilot provider, Body body, Vector2 spd, ManiObject owner,
                     boolean controlsEnabled, float mass)
  {
    boolean working = applyInput(game, angle, provider, body, spd, controlsEnabled, mass);

    myFlameSrc1.setWorking(working);
    myFlameSrc2.setWorking(working);

    myLightSrc1.update(working, angle, game);
    myLightSrc2.update(working, angle, game);
    if (working) {
      ManiSound sound = myItem.getWorkSound();
      game.getSoundMan().play(game, sound, myFlameSrc1.getPos(), owner); // hack with pos
    }
  }

  private boolean applyInput(ManiGame cmp, float shipAngle, Pilot provider, Body body, Vector2 spd,
                             boolean controlsEnabled, float mass)
  {
    boolean spdOk = ManiMath.canAccelerate(shipAngle, spd);
    boolean working = controlsEnabled && provider.isUp() && spdOk;

    EngineItem e = myItem;
    if (working) {
      Vector2 v = ManiMath.fromAl(shipAngle, mass * e.getAcc());
      body.applyForceToCenter(v, true);
      ManiMath.free(v);
    }

    float ts = cmp.getTimeStep();
    float rotSpd = body.getAngularVelocity() * ManiMath.radDeg;
    float desiredRotSpd = 0;
    float rotAcc = e.getRotAcc();
    boolean l = controlsEnabled && provider.isLeft();
    boolean r = controlsEnabled && provider.isRight();
    float absRotSpd = ManiMath.abs(rotSpd);
    if (absRotSpd < e.getMaxRotSpd() && l != r) {
      desiredRotSpd = ManiMath.toInt(r) * e.getMaxRotSpd();
      if (absRotSpd < MAX_RECOVER_ROT_SPD) {
        if (myRecoverAwait > 0) myRecoverAwait -= ts;
        if (myRecoverAwait <= 0) rotAcc *= RECOVER_MUL;
      }
    } else {
      myRecoverAwait = RECOVER_AWAIT;
    }
    body.setAngularVelocity(ManiMath.degRad * ManiMath.approach(rotSpd, desiredRotSpd, rotAcc * ts));
    return working;
  }

  public void onRemove(ManiGame game, Vector2 basePos) {
    PartMan pm = game.getPartMan();
    pm.finish(game, myFlameSrc1, basePos);
    pm.finish(game, myFlameSrc2, basePos);
  }

  public EngineItem getItem() {
    return myItem;
  }
}
