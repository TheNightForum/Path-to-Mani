

package org.burntgameproductions.PathToMani.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.Faction;
import org.burntgameproductions.PathToMani.game.item.ItemManager;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.planet.Planet;
import org.burntgameproductions.PathToMani.game.AbilityCommonConfig;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class Teleport implements ShipAbility {
  public static final int MAX_RADIUS = 4;
  public static final String TEX_PATH = "smallGameObjs/teleportBlip";
  private final Vector2 myNewPos;
  private final Config myConfig;
  private boolean myShouldTeleport;
  private float myAngle;

  public Teleport(Config config) {
    myConfig = config;
    myNewPos = new Vector2();
  }

  @Override
  public boolean update(ManiGame game, ManiShip owner, boolean tryToUse) {
    myShouldTeleport = false;
    if (!tryToUse) return false;
    Vector2 pos = owner.getPosition();
    Faction faction = owner.getPilot().getFaction();
    ManiShip ne = game.getFactionMan().getNearestEnemy(game, MAX_RADIUS, faction, pos);
    if (ne == null) return false;
    Vector2 nePos = ne.getPosition();
    Planet np = game.getPlanetMan().getNearestPlanet();
    if (np.isNearGround(nePos)) return false;
    for (int i = 0; i < 5; i++) {
      myNewPos.set(pos);
      myNewPos.sub(nePos);
      myAngle = myConfig.angle * ManiMath.rnd(.5f, 1) * ManiMath.toInt(ManiMath.test(.5f));
      ManiMath.rotate(myNewPos, myAngle);
      myNewPos.add(nePos);
      if (game.isPlaceEmpty(myNewPos, false)) {
        myShouldTeleport = true;
        return true;
      }
    }
    return false;
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

  // can be performed in update
  public void maybeTeleport(ManiGame game, ManiShip owner) {
    if (!myShouldTeleport) return;

    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex(TEX_PATH, null);
    float blipSz = owner.getHull().config.getApproxRadius() * 3;
    game.getPartMan().blip(game, owner.getPosition(), ManiMath.rnd(180), blipSz, 1, Vector2.Zero, tex);
    game.getPartMan().blip(game, myNewPos, ManiMath.rnd(180), blipSz, 1, Vector2.Zero, tex);

    float newAngle = owner.getAngle() + myAngle;
    Vector2 newSpd = ManiMath.getVec(owner.getSpd());
    ManiMath.rotate(newSpd, myAngle);

    Body body = owner.getHull().getBody();
    body.setTransform(myNewPos, newAngle * ManiMath.degRad);
    body.setLinearVelocity(newSpd);

    ManiMath.free(newSpd);
  }

  public static class Config implements AbilityConfig {
    private final float angle;
    private final ManiItem chargeExample;
    private final float rechargeTime;
    private final AbilityCommonConfig cc;

    public Config(float angle, ManiItem chargeExample, float rechargeTime, AbilityCommonConfig cc) {
      this.angle = angle;
      this.chargeExample = chargeExample;
      this.rechargeTime = rechargeTime;
      this.cc = cc;
    }

    public ShipAbility build() {
      return new Teleport(this);
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
      sb.append("Teleport around enemy");
    }

    public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc) {
      float angle = abNode.getFloat("angle");
      ManiItem chargeExample = itemManager.getExample("teleportCharge");
      float rechargeTime = abNode.getFloat("rechargeTime");
      return new Config(angle, chargeExample, rechargeTime, cc);
    }
  }
}
