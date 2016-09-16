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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.*;
import com.pathtomani.managers.dra.Dra;
import com.pathtomani.entities.gun.GunItem;
import com.pathtomani.entities.ship.hulls.HullConfig;
import com.pathtomani.managers.sound.ManiSound;
import com.pathtomani.managers.sound.SoundManager;
import com.pathtomani.game.ManiObject;
import com.pathtomani.entities.gun.GunMount;
import com.pathtomani.managers.input.Pilot;
import com.pathtomani.entities.item.Armor;
import com.pathtomani.entities.item.ItemContainer;
import com.pathtomani.entities.item.Shield;
import com.pathtomani.entities.item.TradeContainer;
import com.pathtomani.entities.item.Loot;
import com.pathtomani.entities.item.ManiItem;
import com.pathtomani.entities.item.MoneyItem;
import com.pathtomani.entities.item.RepairItem;
import com.pathtomani.entities.item.EngineItem;
import com.pathtomani.gfx.particle.ParticleSrc;
import com.pathtomani.entities.ship.hulls.Hull;
import com.pathtomani.managers.sound.SpecialSounds;

import java.util.List;

public class ManiShip implements ManiObject {
  public static final float BASE_DUR_MOD = .3f;
  public static final float PULL_DIST = 2f;
  public static final float SMOKE_PERC = .6f;
  public static final float FIRE_PERC = .3f;
  private static final int TRADE_AFTER = 3;
  public static final float MAX_FIRE_AWAIT = 1f;
  private static final float ENERGY_DMG_FACTOR = .7f;

  private final Pilot myPilot;
  private final ItemContainer myItemContainer;
  private final TradeContainer myTradeContainer;
  private final Hull myHull;
  private final ParticleSrc mySmokeSrc;
  private final ParticleSrc myFireSrc;
  private final ParticleSrc myElectricitySrc;
  private final RemoveController myRemoveController;
  private final List<Dra> myDras;
  private final ShipRepairer myRepairer;
  private final ShipAbility myAbility;

  private Shield myShield;
  private float myMoney;
  private float myIdleTime;
  private Armor myArmor;
  private float myFireAwait;
  private float myAbilityAwait;
  private float myControlEnableAwait;

  public ManiShip(ManiGame game, Pilot pilot, Hull hull, RemoveController removeController, List<Dra> dras,
                  ItemContainer container, ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield,
                  Armor armor)
  {
    myRemoveController = removeController;
    myDras = dras;
    myPilot = pilot;
    myHull = hull;
    myItemContainer = container;
    myTradeContainer = tradeContainer;
    List<ParticleSrc> effs = game.getSpecialEffects().buildBodyEffs(myHull.config.getApproxRadius(), game, myHull.getPos(), myHull.getSpd());
    mySmokeSrc = effs.get(0);
    myFireSrc = effs.get(1);
    myElectricitySrc = effs.get(2);
    myDras.add(mySmokeSrc);
    myDras.add(myFireSrc);
    myDras.add(myElectricitySrc);
    myRepairer = repairer;
    myMoney = money;
    myShield = shield;
    myArmor = armor;
    AbilityConfig ac = myHull.config.getAbility();
    myAbility = ac == null ? null : ac.build();
    if (myAbility != null) {
      myAbilityAwait = myAbility.getConfig().getRechargeTime();
    }
  }

  @Override
  public Vector2 getPosition() {
    return myHull.getPos();
  }

  @Override
  public FarShip toFarObj() {
    float rotSpd = myHull.getBody().getAngularVelocity() * ManiMath.radDeg;
    return new FarShip(myHull.getPos(), myHull.getSpd(), myHull.getAngle(), rotSpd, myPilot, myItemContainer, myHull.config, myHull.life,
      myHull.getGun(false), myHull.getGun(true), myRemoveController, myHull.getEngine(), myRepairer, myMoney, myTradeContainer, myShield, myArmor);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                            ManiGame game, Vector2 collPos)
  {
    if (tryCollectLoot(other, game)) {
      ((Loot)other).pickedUp(game, this);
      return;
    }
    if (myHull.config.getType() != HullConfig.Type.STATION) {
      Fixture f = null; // restore?
      float dmg = absImpulse / myHull.getMass() / myHull.config.getDurability();
      if (f == myHull.getBase()) {
        dmg *= BASE_DUR_MOD;
      }
      receiveDmg((int) dmg, game, collPos, DmgType.CRASH);
    }
  }

  @Override
  public String toDebugString() {
    return myPilot.toDebugString();
  }

  @Override
  public Boolean isMetal() {
    return true;
  }

  @Override
  public boolean hasBody() {
    return true;
  }

  private boolean tryCollectLoot(ManiObject obj, ManiGame game) {
    if (!(obj instanceof Loot)) {
      return false;
    }
    if (!myPilot.collectsItems()) {
      return false;
    }
    Loot loot = (Loot) obj;
    if (loot.getOwner() == this) {
      return false;
    }
    ManiItem i = loot.getItem();
    if (i == null) {
      return false;
    }
    i.setEquipped(0);
    if (i instanceof MoneyItem) {
      myMoney += i.getPrice();
      return true;
    }
    ItemContainer c = shouldTrade(i, game) ? myTradeContainer.getItems() : myItemContainer;
    boolean canAdd = c.canAdd(i);
    if (canAdd) {
      c.add(i);
      if(c == myItemContainer&&myPilot.getMapHint()=="Merc")
      {
    	  //System.out.println("Merc could try to equip");
    	  //insert equip code here, if it's something we want to do
      }
    }
    return canAdd;
  }

  private boolean shouldTrade(ManiItem i, ManiGame game) {
    if (myTradeContainer == null) {
      return false;
    }
    if (i instanceof RepairItem) {
      return myItemContainer.count(game.getItemMan().getRepairExample()) >= TRADE_AFTER;
    }
    GunItem g1 = myHull.getGun(false);
    if (g1 != null && g1.config.clipConf.example.isSame(i)) {
      return myItemContainer.count(g1.config.clipConf.example) >= TRADE_AFTER;
    }
    GunItem g2 = myHull.getGun(true);
    if (g2 != null && g2.config.clipConf.example.isSame(i)) {
      return myItemContainer.count(g2.config.clipConf.example) >= TRADE_AFTER;
    }
    return true;
  }

  public Vector2 getSpd() {
    return myHull.getSpd();
  }

  public float getAngle() {
    return myHull.getAngle();
  }

  public float getAcc() {
    EngineItem e = myHull.getEngine();
    return e == null ? 0 : e.getAcc();
  }

  @Override
  public void update(ManiGame game) {
    ManiShip nearestEnemy = game.getFactionMan().getNearestEnemy(game, this);
    myPilot.update(game, this, nearestEnemy);
    myHull.update(game, myItemContainer, myPilot, this, nearestEnemy);

    updateAbility(game);
    updateIdleTime(game);
    updateShield(game);
    if (myArmor != null && !myItemContainer.contains(myArmor)) {
      myArmor = null;
    }
    if (myTradeContainer != null) {
      myTradeContainer.update(game);
    }

    if (isControlsEnabled() && myRepairer != null && myIdleTime > ShipRepairer.REPAIR_AWAIT) {
      myHull.life += myRepairer.tryRepair(game, myItemContainer, myHull.life, myHull.config);
    }

    float ts = game.getTimeStep();
    if (myFireAwait > 0) {
      myFireAwait -= ts;
    }
    mySmokeSrc.setWorking(myFireAwait > 0 || myHull.life < SMOKE_PERC * myHull.config.getMaxLife());
    boolean onFire = myFireAwait > 0 || myHull.life < FIRE_PERC * myHull.config.getMaxLife();
    myFireSrc.setWorking(onFire);
    if (onFire) {
      game.getSoundMan().play(game, game.getSpecialSounds().burning, null, this);
    }

    if (!isControlsEnabled()) {
      myControlEnableAwait -= ts;
      if (isControlsEnabled()) {
        game.getSoundMan().play(game, game.getSpecialSounds().controlEnabled, null, this);
      }
    }
    myElectricitySrc.setWorking(!isControlsEnabled());

    if (myAbility instanceof Teleport) {
      ((Teleport) myAbility).maybeTeleport(game, this);
    }
  }

  private void updateAbility(ManiGame game) {
    if (myAbility == null) {
      return;
    }
    SoundManager soundManager = game.getSoundMan();
    SpecialSounds sounds = game.getSpecialSounds();
    if (myAbilityAwait > 0) {
      myAbilityAwait -= game.getTimeStep();
      if (myAbilityAwait <= 0) {
        soundManager.play(game, sounds.abilityRecharged, null, this);
      }
    }
    boolean tryToUse = isControlsEnabled() && myPilot.isAbility() && canUseAbility();
    boolean used = myAbility.update(game, this, tryToUse);
    if (used) {
      ManiItem example = myAbility.getConfig().getChargeExample();
      if (example != null) {
        myItemContainer.tryConsumeItem(example);
      }
      myAbilityAwait = myAbility.getConfig().getRechargeTime();
      AbilityCommonConfig cc = myAbility.getCommonConfig();
      soundManager.play(game, cc.activatedSound, null, this);
    }
    if (tryToUse && !used) {
      soundManager.play(game, sounds.abilityRefused, null, this);
    }
  }

  private void updateShield(ManiGame game) {
    if (myShield != null) {
      if (myItemContainer.contains(myShield)) {
        myShield.update(game, this);
      } else {
        myShield = null;
      }
    }
  }

  private void updateIdleTime(ManiGame game) {
    float ts = game.getTimeStep();
    if (Pilot.Utils.isIdle(myPilot)) {
      myIdleTime += ts;
    } else {
      myIdleTime = 0;
    }
  }

  public boolean canUseAbility() {
    if (myAbility == null || myAbilityAwait > 0) {
      return false;
    }
    ManiItem example = myAbility.getConfig().getChargeExample();
    if (example == null) {
      return true;
    }
    return myItemContainer.count(example) > 0;
  }

  public float getPullDist() {
    return PULL_DIST + myHull.config.getApproxRadius();
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return myHull.life <= 0 || myRemoveController != null && myRemoveController.shouldRemove(myHull.getPos());
  }

  @Override
  public void onRemove(ManiGame game) {
    if (myHull.life <= 0) {
      game.getShardBuilder().buildExplosionShards(game, myHull.getPos(), myHull.getSpd(), myHull.config.getSize());
      throwAllLoot(game);
    }
    myHull.onRemove(game);
    game.getPartMan().finish(game, mySmokeSrc, myHull.getPos());
    game.getPartMan().finish(game, myFireSrc, myHull.getPos());
  }

  private void throwAllLoot(ManiGame game) {
    if (myPilot.isPlayer()) {
      game.beforeHeroDeath();
    }

    for (List<ManiItem> group : myItemContainer) {
      for (ManiItem item : group) {
        float dropChance = maybeUnequip(game, item, false) ? .35f : .6f;
        if (ManiMath.test(dropChance)) {
          throwLoot(game, item, true);
        }
      }
    }

    if (myTradeContainer != null) {
      for (List<ManiItem> group : myTradeContainer.getItems()) {
        for (ManiItem item : group) {
          if (ManiMath.test(.6f)) {
            throwLoot(game, item, true);
          }
        }
      }
    }
    float thrMoney = myMoney * ManiMath.rnd(.2f, 1);
    List<MoneyItem> moneyItems = game.getItemMan().moneyToItems(thrMoney);
    for (MoneyItem mi : moneyItems) {
      throwLoot(game, mi, true);
    }
  }

  private void throwLoot(ManiGame game, ManiItem item, boolean onDeath) {
    Vector2 lootSpd = new Vector2();
    float spdAngle;
    float spdLen;
    Vector2 pos = new Vector2();
    if (onDeath) {
      spdAngle = ManiMath.rnd(180);
      spdLen = ManiMath.rnd(0, Loot.MAX_SPD);
      // TODO: This statement previously caused a crash as getApproxRadius returned 0 - where is it meant to be set / loaded from?
      ManiMath.fromAl(pos, spdAngle, ManiMath.rnd(myHull.config.getApproxRadius()));
    } else {
      spdAngle = getAngle();
      spdLen = 1f;
      ManiMath.fromAl(pos, spdAngle, myHull.config.getApproxRadius());
    }
    ManiMath.fromAl(lootSpd, spdAngle, spdLen);
    lootSpd.add(myHull.getSpd());
    pos.add(myHull.getPos());
    Loot l = game.getLootBuilder().build(game, pos, item, lootSpd, Loot.MAX_LIFE, ManiMath.rnd(Loot.MAX_ROT_SPD), this);
    game.getObjMan().addObjDelayed(l);
    if (!onDeath) {
      game.getSoundMan().play(game, game.getSpecialSounds().lootThrow, pos, this);
    }
  }

  @Override
  public void receiveDmg(float dmg, ManiGame game, Vector2 pos, DmgType dmgType) {
    if (dmg <= 0) {
      return;
    }
    if (myShield != null && myShield.canAbsorb(dmgType)) {
      myShield.absorb(game, dmg, pos, this, dmgType);
      return;
    }
    if (myArmor != null) {
      if (dmgType == DmgType.ENERGY) {
        dmg *= ENERGY_DMG_FACTOR;
      }
      dmg *= (1 - myArmor.getPerc());
    }
    playHitSound(game, pos, dmgType);

    boolean wasAlive = myHull.life > 0;
    myHull.life -= dmg;
    if (wasAlive && myHull.life <= 0) {
      Vector2 shipPos = getPosition();
      game.getSpecialEffects().explodeShip(game, shipPos, myHull.config.getSize());
      game.getSoundMan().play(game, game.getSpecialSounds().shipExplosion, null, this);
    }
    if (dmgType == DmgType.FIRE) {
      myFireAwait = MAX_FIRE_AWAIT;
    }
  }

  private void playHitSound(ManiGame game, Vector2 pos, DmgType dmgType) {
    if (myArmor != null) {
      ManiSound sound = myArmor.getHitSound(dmgType);
      game.getSoundMan().play(game, sound, pos, this);
    } else {
      game.getSpecialSounds().playHit(game, this, pos, dmgType);
    }
  }

  @Override
  public boolean receivesGravity() {
    return true;
  }

  @Override
  public void receiveForce(Vector2 force, ManiGame game, boolean acc) {
    Body body = myHull.getBody();
    if (acc) {
      force.scl(myHull.getMass());
    }
    body.applyForceToCenter(force, true);
  }

  public ItemContainer getItemContainer() {
    return myItemContainer;
  }

  public float getLife() {
    return myHull.life;
  }

  public Pilot getPilot() {
    return myPilot;
  }

  public float getRotSpd() { return myHull.getRotSpd(); }

  public float getRotAcc() {
    EngineItem e = myHull.getEngine();
    return e == null ? 0 : e.getRotAcc();
  }

  public Hull getHull() {
    return myHull;
  }

  public float calcTimeToTurn(float destAngle) {
    float angle = myHull.getAngle();
    EngineItem e = myHull.getEngine();
    float ad = ManiMath.angleDiff(angle, destAngle);
    return ad/e.getMaxRotSpd();
  }

  public boolean maybeEquip(ManiGame game, ManiItem item, boolean equip) {
    return maybeEquip(game, item, false, equip) || maybeEquip(game, item, true, equip);
  }

  public boolean maybeEquip(ManiGame game, ManiItem item, boolean secondarySlot, boolean equip) {
    if (!secondarySlot) {
      if (item instanceof EngineItem) {
        if (true) {
          Gdx.app.log("ManiShip", "maybeEquip called for an engine item, can't do that!");
          //throw new AssertionError("engine items not supported");
        }
        EngineItem ei = (EngineItem) item;
        boolean ok = ei.isBig() == (myHull.config.getType() == HullConfig.Type.BIG);
        if (ok && equip) myHull.setEngine(game, this, ei);
        return ok;
      }
      if (item instanceof Shield) {
        Shield shield = (Shield) item;
        if (equip) {
          maybeUnequip(game, myShield, false, true);
          myShield = shield;
          myShield.setEquipped(1);
        }
        return true;
      }
      if (item instanceof Armor) {
        Armor armor = (Armor) item;
        if (equip) {
          maybeUnequip(game, myArmor, false, true);
          myArmor = armor;
          myArmor.setEquipped(1);
        }
        return true;
      }
    }
    if (item instanceof GunItem) {
      GunItem gun = (GunItem) item;
      GunMount mount = myHull.getGunMount(secondarySlot);
      boolean canEquip = mount != null && (gun.config.fixed == mount.isFixed());
      if (canEquip && equip) {
        GunMount anotherMount = myHull.getGunMount(!secondarySlot);
        if (anotherMount != null && anotherMount.getGun() == item) {
          anotherMount.setGun(game, this, null, false, 0);
        }
        final int slotNr = secondarySlot ? 1 : 0;
        boolean under = myHull.config.getGunSlot(slotNr).isUnderneathHull();
        mount.setGun(game, this, gun, under, slotNr + 1);
      }
      return canEquip;
    }
    return false;
  }

  public boolean maybeUnequip(ManiGame game, ManiItem item, boolean unequip) {
    return maybeUnequip(game, item, false, unequip) || maybeUnequip(game, item, true, unequip);
  }

  public boolean maybeUnequip(ManiGame game, ManiItem item, boolean secondarySlot, boolean unequip) {
    if (!secondarySlot) {
      if (myHull.getEngine() == item) {
        if (true) {
          Gdx.app.log("ManiShip", "maybeUnequip called for an engine item, can't do that!");
          //throw new AssertionError("engine items not supported");
        }
        if (unequip) {
          myHull.setEngine(game, this, null);
        }
        return true;
      }
      if (myShield == item) {
        if (unequip && myShield != null) {
          myShield.setEquipped(0);
          myShield = null;
        }
        return true;
      }
      if (myArmor == item) {
        if (unequip && myArmor != null) {
          myArmor.setEquipped(0);
          myArmor = null;
        }
        return true;
      }
    }
    GunMount m = myHull.getGunMount(secondarySlot);
    if (m != null && m.getGun() == item) {
      if (unequip) {
        m.setGun(game, this, null, false, 0);
      }
      return true;
    }
    return false;
  }

  public float getRepairPoints() {
    return myRepairer == null ? 0 : myRepairer.getRepairPoints();
  }

  public float getMoney() {
    return myMoney;
  }

  public TradeContainer getTradeContainer() {
    return myTradeContainer;
  }

  public void setMoney(float money) {
    myMoney = money;
  }

  public Shield getShield() {
    return myShield;
  }

  public Armor getArmor() {
    return myArmor;
  }

  public ShipAbility getAbility() {
    return myAbility;
  }

  public void disableControls(float duration, ManiGame game) {
    if (myControlEnableAwait <= 0) {
      game.getSoundMan().play(game, game.getSpecialSounds().controlDisabled, null, this);
    }
    myControlEnableAwait += duration;
  }

  public boolean isControlsEnabled() {
    return myControlEnableAwait <= 0;
  }

  public void dropItem(ManiGame game, ManiItem item) {
    myItemContainer.remove(item);
    throwLoot(game, item, false);
  }

  public float getAbilityAwait() {
    return myAbilityAwait;
  }
}
