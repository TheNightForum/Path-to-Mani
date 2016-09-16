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

package com.pathtomani.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.AbilityCommonConfig;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.ManiObject;
import com.pathtomani.game.item.SolItem;
import com.pathtomani.game.DmgType;
import com.pathtomani.game.dra.DraLevel;
import com.pathtomani.game.item.ItemManager;
import com.pathtomani.game.item.Shield;
import com.pathtomani.game.particle.ParticleSrc;

public class UnShield implements ShipAbility {
  public static final int MAX_RADIUS = 6;
  private final Config myConfig;

  public UnShield(Config config) {
    myConfig = config;
  }

  @Override
  public AbilityConfig getConfig() {
    return myConfig;
  }

  @Override
  public AbilityCommonConfig getCommonConfig() {
    return myConfig.cc;
  }

  @Override
  public float getRadius() {
    return MAX_RADIUS;
  }

  @Override
  public boolean update(ManiGame game, ManiShip owner, boolean tryToUse) {
    if (!tryToUse) return false;
    Vector2 ownerPos = owner.getPosition();
    for (ManiObject o : game.getObjMan().getObjs()) {
      if (!(o instanceof ManiShip) || o == owner) continue;
      ManiShip oShip = (ManiShip) o;
      Shield shield = oShip.getShield();
      if (shield == null) continue;
      float shieldLife = shield.getLife();
      if (shieldLife <= 0) continue;
      if (!game.getFactionMan().areEnemies(oShip, owner)) continue;
      Vector2 oPos = o.getPosition();
      float dst = oPos.dst(ownerPos);
      float perc = KnockBack.getPerc(dst, MAX_RADIUS);
      if (perc <= 0) continue;
      float amount = perc * myConfig.amount;
      if (shieldLife < amount) amount = shieldLife;
      oShip.receiveDmg(amount, game, ownerPos, DmgType.ENERGY);
    }
    ParticleSrc src = new ParticleSrc(myConfig.cc.effect, MAX_RADIUS, DraLevel.PART_BG_0, new Vector2(), true, game, ownerPos, Vector2.Zero, 0);
    game.getPartMan().finish(game, src, ownerPos);
    return true;
  }


  public static class Config implements AbilityConfig {
    public final float rechargeTime;
    private final SolItem chargeExample;
    public final float amount;
    private final AbilityCommonConfig cc;

    public Config(float rechargeTime, SolItem chargeExample, float amount, AbilityCommonConfig cc) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.amount = amount;
      this.cc = cc;
    }

    @Override
    public ShipAbility build() {
      return new UnShield(this);
    }

    @Override
    public SolItem getChargeExample() {
      return chargeExample;
    }

    @Override
    public float getRechargeTime() {
      return rechargeTime;
    }

    @Override
    public void appendDesc(StringBuilder sb) {
      sb.append("Deal ").append(ManiMath.nice(amount)).append(" dmg to enemy shields\n");
    }

    public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float amount = abNode.getFloat("amount");
      SolItem chargeExample = itemManager.getExample("unShieldCharge");
      return new Config(rechargeTime, chargeExample, amount, cc);
    }
  }
}
