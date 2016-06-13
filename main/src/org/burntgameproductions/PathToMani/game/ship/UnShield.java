

package org.burntgameproductions.PathToMani.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.AbilityCommonConfig;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.dra.DraLevel;
import org.burntgameproductions.PathToMani.game.item.ItemManager;
import org.burntgameproductions.PathToMani.game.item.Shield;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.particle.ParticleSrc;
import org.burntgameproductions.PathToMani.game.DmgType;
import org.burntgameproductions.PathToMani.game.ManiGame;

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
    private final ManiItem chargeExample;
    public final float amount;
    private final AbilityCommonConfig cc;

    public Config(float rechargeTime, ManiItem chargeExample, float amount, AbilityCommonConfig cc) {
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
    public ManiItem getChargeExample() {
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
      ManiItem chargeExample = itemManager.getExample("unShieldCharge");
      return new Config(rechargeTime, chargeExample, amount, cc);
    }
  }
}
