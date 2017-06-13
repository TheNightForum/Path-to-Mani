/*
 * Copyright 2017 TheNightForum
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

package com.tnf.ptm.entities.ship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.tnf.ptm.common.*;
import com.tnf.ptm.handler.AbilityCommonConfig;
import com.tnf.ptm.entities.gun.GunMount;
import com.tnf.ptm.entities.item.Engine;
import com.tnf.ptm.entities.item.Loot;
import com.tnf.ptm.entities.item.RepairItem;
import com.tnf.ptm.entities.item.PtmItem;
import com.tnf.ptm.entities.ship.hulls.HullConfig;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.handler.input.Pilot;
import com.tnf.ptm.entities.item.Armor;
import com.tnf.ptm.entities.item.Gun;
import com.tnf.ptm.entities.item.ItemContainer;
import com.tnf.ptm.entities.item.MoneyItem;
import com.tnf.ptm.entities.item.Shield;
import com.tnf.ptm.entities.item.TradeContainer;
import com.tnf.ptm.gfx.particle.ParticleSrc;
import com.tnf.ptm.entities.ship.hulls.Hull;
import com.tnf.ptm.sound.OggSoundManager;
import com.tnf.ptm.sound.SpecialSounds;

import java.util.List;

public class PtmShip implements PtmObject {
    public static final float BASE_DUR_MOD = .3f;
    public static final float PULL_DIST = 2f;
    public static final float SMOKE_PERC = .6f;
    public static final float FIRE_PERC = .3f;
    public static final float MAX_FIRE_AWAIT = 1f;
    private static final int TRADE_AFTER = 3;
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

    public PtmShip(PtmGame game, Pilot pilot, Hull hull, RemoveController removeController, List<Dra> dras,
                   ItemContainer container, ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield,
                   Armor armor) {
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
        float rotSpd = myHull.getBody().getAngularVelocity() * PtmMath.radDeg;
        return new FarShip(myHull.getPos(), myHull.getSpd(), myHull.getAngle(), rotSpd, myPilot, myItemContainer, myHull.config, myHull.life,
                myHull.getGun(false), myHull.getGun(true), myRemoveController, myHull.getEngine(), myRepairer, myMoney, myTradeContainer, myShield, myArmor);
    }

    @Override
    public List<Dra> getDras() {
        return myDras;
    }

    @Override
    public void handleContact(PtmObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              PtmGame game, Vector2 collPos) {
        if (tryCollectLoot(other, game)) {
            ((Loot) other).pickedUp(game, this);
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

    private boolean tryCollectLoot(PtmObject obj, PtmGame game) {
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
        PtmItem i = loot.getItem();
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
            if (c == myItemContainer && myPilot.getMapHint() == "Merc") {
                //insert equip code here, if it's something we want to do
            }
        }
        return canAdd;
    }

    private boolean shouldTrade(PtmItem i, PtmGame game) {
        if (myTradeContainer == null) {
            return false;
        }
        if (i instanceof RepairItem) {
            return myItemContainer.count(game.getItemMan().getRepairExample()) >= TRADE_AFTER;
        }
        Gun g1 = myHull.getGun(false);
        if (g1 != null && g1.config.clipConf.example.isSame(i)) {
            return myItemContainer.count(g1.config.clipConf.example) >= TRADE_AFTER;
        }
        Gun g2 = myHull.getGun(true);
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
        Engine e = myHull.getEngine();
        return e == null ? 0 : e.getAcc();
    }

    @Override
    public void update(PtmGame game) {
        PtmShip nearestEnemy = game.getFactionMan().getNearestEnemy(game, this);
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
            game.getSoundManager().play(game, game.getSpecialSounds().burning, null, this);
        }

        if (!isControlsEnabled()) {
            myControlEnableAwait -= ts;
            if (isControlsEnabled()) {
                game.getSoundManager().play(game, game.getSpecialSounds().controlEnabled, null, this);
            }
        }
        myElectricitySrc.setWorking(!isControlsEnabled());

        if (myAbility instanceof Teleport) {
            ((Teleport) myAbility).maybeTeleport(game, this);
        }
    }

    private void updateAbility(PtmGame game) {
        if (myAbility == null) {
            return;
        }
        OggSoundManager soundManager = game.getSoundManager();
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
            PtmItem example = myAbility.getConfig().getChargeExample();
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

    private void updateShield(PtmGame game) {
        if (myShield != null) {
            if (myItemContainer.contains(myShield)) {
                myShield.update(game, this);
            } else {
                myShield = null;
            }
        }
    }

    private void updateIdleTime(PtmGame game) {
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
        PtmItem example = myAbility.getConfig().getChargeExample();
        if (example == null) {
            return true;
        }
        return myItemContainer.count(example) > 0;
    }

    public float getPullDist() {
        return PULL_DIST + myHull.config.getApproxRadius();
    }

    @Override
    public boolean shouldBeRemoved(PtmGame game) {
        return myHull.life <= 0 || myRemoveController != null && myRemoveController.shouldRemove(myHull.getPos());
    }

    @Override
    public void onRemove(PtmGame game) {
        if (myHull.life <= 0) {
            game.getShardBuilder().buildExplosionShards(game, myHull.getPos(), myHull.getSpd(), myHull.config.getSize());
            throwAllLoot(game);
        }
        myHull.onRemove(game);
        game.getPartMan().finish(game, mySmokeSrc, myHull.getPos());
        game.getPartMan().finish(game, myFireSrc, myHull.getPos());
    }

    private void throwAllLoot(PtmGame game) {
        if (myPilot.isPlayer()) {
            game.beforeHeroDeath();
        }

        for (List<PtmItem> group : myItemContainer) {
            for (PtmItem item : group) {
                float dropChance = maybeUnequip(game, item, false) ? .35f : .6f;
                if (PtmMath.test(dropChance)) {
                    throwLoot(game, item, true);
                }
            }
        }

        if (myTradeContainer != null) {
            for (List<PtmItem> group : myTradeContainer.getItems()) {
                for (PtmItem item : group) {
                    if (PtmMath.test(.6f)) {
                        throwLoot(game, item, true);
                    }
                }
            }
        }
        float thrMoney = myMoney * PtmMath.rnd(.2f, 1);
        List<MoneyItem> moneyItems = game.getItemMan().moneyToItems(thrMoney);
        for (MoneyItem mi : moneyItems) {
            throwLoot(game, mi, true);
        }
    }

    private void throwLoot(PtmGame game, PtmItem item, boolean onDeath) {
        Vector2 lootSpd = new Vector2();
        float spdAngle;
        float spdLen;
        Vector2 pos = new Vector2();
        if (onDeath) {
            spdAngle = PtmMath.rnd(180);
            spdLen = PtmMath.rnd(0, Loot.MAX_SPD);
            // TODO: This statement previously caused a crash as getApproxRadius returned 0 - where is it meant to be set / loaded from?
            PtmMath.fromAl(pos, spdAngle, PtmMath.rnd(myHull.config.getApproxRadius()));
        } else {
            spdAngle = getAngle();
            spdLen = 1f;
            PtmMath.fromAl(pos, spdAngle, myHull.config.getApproxRadius());
        }
        PtmMath.fromAl(lootSpd, spdAngle, spdLen);
        lootSpd.add(myHull.getSpd());
        pos.add(myHull.getPos());
        Loot l = game.getLootBuilder().build(game, pos, item, lootSpd, Loot.MAX_LIFE, PtmMath.rnd(Loot.MAX_ROT_SPD), this);
        game.getObjMan().addObjDelayed(l);
        if (!onDeath) {
            game.getSoundManager().play(game, game.getSpecialSounds().lootThrow, pos, this);
        }
    }

    @Override
    public void receiveDmg(float dmg, PtmGame game, Vector2 pos, DmgType dmgType) {
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
            game.getSoundManager().play(game, game.getSpecialSounds().shipExplosion, null, this);
        }
        if (dmgType == DmgType.FIRE) {
            myFireAwait = MAX_FIRE_AWAIT;
        }
    }

    private void playHitSound(PtmGame game, Vector2 pos, DmgType dmgType) {
        if (myArmor != null) {
            game.getSoundManager().play(game, myArmor.getHitSound(dmgType), pos, this);
        } else {
            game.getSpecialSounds().playHit(game, this, pos, dmgType);
        }
    }

    @Override
    public boolean receivesGravity() {
        return true;
    }

    @Override
    public void receiveForce(Vector2 force, PtmGame game, boolean acc) {
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

    public float getRotSpd() {
        return myHull.getRotSpd();
    }

    public float getRotAcc() {
        Engine e = myHull.getEngine();
        return e == null ? 0 : e.getRotAcc();
    }

    public Hull getHull() {
        return myHull;
    }

    public float calcTimeToTurn(float destAngle) {
        float angle = myHull.getAngle();
        Engine e = myHull.getEngine();
        float ad = PtmMath.angleDiff(angle, destAngle);
        return ad / e.getMaxRotSpd();
    }

    public boolean maybeEquip(PtmGame game, PtmItem item, boolean equip) {
        return maybeEquip(game, item, false, equip) || maybeEquip(game, item, true, equip);
    }

    public boolean maybeEquip(PtmGame game, PtmItem item, boolean secondarySlot, boolean equip) {
        if (!secondarySlot) {
            if (item instanceof Engine) {
                if (true) {
                    Gdx.app.log("PtmShip", "maybeEquip called for an engine item, can't do that!");
                    //throw new AssertionError("engine items not supported");
                }
                Engine ei = (Engine) item;
                boolean ok = ei.isBig() == (myHull.config.getType() == HullConfig.Type.BIG);
                if (ok && equip) {
                    myHull.setEngine(game, this, ei);
                }
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
        if (item instanceof Gun) {
            Gun gun = (Gun) item;
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

    public boolean maybeUnequip(PtmGame game, PtmItem item, boolean unequip) {
        return maybeUnequip(game, item, false, unequip) || maybeUnequip(game, item, true, unequip);
    }

    public boolean maybeUnequip(PtmGame game, PtmItem item, boolean secondarySlot, boolean unequip) {
        if (!secondarySlot) {
            if (myHull.getEngine() == item) {
                if (true) {
                    Gdx.app.log("PtmShip", "maybeUnequip called for an engine item, can't do that!");
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

    public void setMoney(float money) {
        myMoney = money;
    }

    public TradeContainer getTradeContainer() {
        return myTradeContainer;
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

    public void disableControls(float duration, PtmGame game) {
        if (myControlEnableAwait <= 0) {
            game.getSoundManager().play(game, game.getSpecialSounds().controlDisabled, null, this);
        }
        myControlEnableAwait += duration;
    }

    public boolean isControlsEnabled() {
        return myControlEnableAwait <= 0;
    }

    public void dropItem(PtmGame game, PtmItem item) {
        myItemContainer.remove(item);
        throwLoot(game, item, false);
    }

    public float getAbilityAwait() {
        return myAbilityAwait;
    }
}
