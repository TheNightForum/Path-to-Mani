/*
 * Copyright 2016 MovingBlocks
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

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.DmgType;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.particle.LightSrc;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

import java.util.List;

public class Loot implements ManiObject {

  public static final int MAX_ROT_SPD = 4;
  public static final float MAX_SPD = .2f;
  public static final int MAX_LIFE = 6;
  public static final float DURABILITY = 70f;
  public static final float PULL_DESIRED_SPD = 1f;
  public static final float PULL_FORCE = .1f;
  public static final float MAX_OWNER_AWAIT = 4f;
  private final ManiItem myItem;
  private final List<Dra> myDras;
  private final LightSrc myLightSrc;
  private final Vector2 myPos;
  private final Body myBody;
  private final float myMass;

  private ManiShip myOwner;
  private float myOwnerAwait;
  private int myLife;
  private float myAngle;

  public Loot(ManiItem item, Body body, int life, List<Dra> dras, LightSrc ls, ManiShip owner) {
    myBody = body;
    myLife = life;
    myItem = item;
    myDras = dras;
    myLightSrc = ls;
    myOwner = owner;
    myOwnerAwait = MAX_OWNER_AWAIT;
    myPos = new Vector2();
    myMass = myBody.getMass();
    setParamsFromBody();
  }

  @Override
  public void update(ManiGame game) {
    setParamsFromBody();
    myLightSrc.update(true, myAngle, game);
    if (myOwnerAwait > 0) {
      myOwnerAwait -= game.getTimeStep();
      if (myOwnerAwait <= 0) myOwner = null;
    }
    ManiShip puller = null;
    float minDist = Float.MAX_VALUE;
    List<ManiObject> objs = game.getObjMan().getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      ManiObject o = objs.get(i);
      if (!(o instanceof ManiShip)) continue;
      ManiShip ship = (ManiShip) o;
      if (!ship.getPilot().collectsItems()) continue;
      if (!(myItem instanceof MoneyItem) && !ship.getItemContainer().canAdd(myItem)) continue;
      float dst = ship.getPosition().dst(myPos);
      if (minDist < dst) continue;
      puller = ship;
      minDist = dst;
    }
    if (puller != null) {
      maybePulled(puller, puller.getPosition(), puller.getPullDist());
    }
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return myLife <= 0;
  }

  @Override
  public void onRemove(ManiGame game) {
    myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public void receiveDmg(float dmg, ManiGame game, Vector2 pos, DmgType dmgType) {
    myLife -= dmg;
    game.getSpecialSounds().playHit(game, this, pos, dmgType);
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveForce(Vector2 force, ManiGame game, boolean acc) {
    if (acc) force.scl(myMass);
    myBody.applyForceToCenter(force, true);
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
    return null;
  }

  @Override
  public void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                            ManiGame game, Vector2 collPos)
  {
    float dmg = absImpulse / myMass / DURABILITY;
    receiveDmg((int) dmg, game, collPos, DmgType.CRASH);
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

  public void maybePulled(ManiShip ship, Vector2 pullerPos, float radius) {
    if (ship == myOwner) return;
    Vector2 toPuller = SolMath.getVec(pullerPos);
    toPuller.sub(getPosition());
    float pullerDist = toPuller.len();
    if (0 < pullerDist && pullerDist < radius) {
      toPuller.scl(PULL_DESIRED_SPD /pullerDist);
      Vector2 spd = myBody.getLinearVelocity();
      Vector2 spdDiff = SolMath.distVec(spd, toPuller);
      float spdDiffLen = spdDiff.len();
      if (spdDiffLen > 0) {
        spdDiff.scl(PULL_FORCE / spdDiffLen);
        myBody.applyForceToCenter(spdDiff, true);
      }
      SolMath.free(spdDiff);
    }
    SolMath.free(toPuller);
  }

  public ManiItem getItem() {
    return myLife > 0 ? myItem : null;
  }

  public void setLife(int life) {
    myLife = life;
  }

  public ManiShip getOwner() {
    return myOwner;
  }

  public void pickedUp(ManiGame game, ManiShip ship) {
    myLife = 0;
    Vector2 spd = new Vector2(ship.getPosition());
    spd.sub(myPos);
    float fadeTime = .25f;
    spd.scl(1 / fadeTime);
    spd.add(ship.getSpd());
    game.getPartMan().blip(game, myPos, myAngle, myItem.getItemType().sz, fadeTime, spd, myItem.getIcon(game));
    game.getSoundMan().play(game, myItem.getItemType().pickUpSound, null, this);
  }
}
