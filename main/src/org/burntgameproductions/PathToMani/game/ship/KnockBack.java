

package org.burntgameproductions.PathToMani.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.ManiObject;
import org.burntgameproductions.PathToMani.game.dra.DraLevel;
import org.burntgameproductions.PathToMani.game.item.ItemManager;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.particle.ParticleSrc;
import org.burntgameproductions.PathToMani.game.AbilityCommonConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class KnockBack implements ShipAbility {
  public static final int MAX_RADIUS = 8;
  private final Config myConfig;

  public KnockBack(Config config) {
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
      if (o == owner || !o.receivesGravity()) continue;
      Vector2 oPos = o.getPosition();
      float dst = oPos.dst(ownerPos);
      if (dst == 0) continue; // O__o
      float perc = getPerc(dst, MAX_RADIUS);
      if (perc <= 0) continue;
      Vector2 toO = ManiMath.distVec(ownerPos, oPos);
      float accLen = myConfig.force * perc;
      toO.scl(accLen / dst);
      o.receiveForce(toO, game, false);
      ManiMath.free(toO);
    }
    ParticleSrc src = new ParticleSrc(myConfig.cc.effect, MAX_RADIUS, DraLevel.PART_BG_0, new Vector2(), true, game, ownerPos, Vector2.Zero, 0);
    game.getPartMan().finish(game, src, ownerPos);
    return true;
  }

  public static float getPerc(float dst, float radius) {
    if (radius < dst) return 0;
    float rHalf = radius / 2;
    if (dst < rHalf) return 1;
    return 1 - (dst - rHalf) / rHalf;
  }


  public static class Config implements AbilityConfig {
    public final float rechargeTime;
    private final ManiItem chargeExample;
    public final float force;
    public final AbilityCommonConfig cc;

    public Config(float rechargeTime, ManiItem chargeExample, float force, AbilityCommonConfig cc) {
      this.rechargeTime = rechargeTime;
      this.chargeExample = chargeExample;
      this.force = force;
      this.cc = cc;
    }

    @Override
    public ShipAbility build() {
      return new KnockBack(this);
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
      sb.append("?\n");
    }

    public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc) {
      float rechargeTime = abNode.getFloat("rechargeTime");
      float force = abNode.getFloat("force");
      ManiItem chargeExample = itemManager.getExample("knockBackCharge");
      return new Config(rechargeTime, chargeExample, force, cc);
    }
  }
}
