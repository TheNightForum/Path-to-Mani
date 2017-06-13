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

package old.tnf.ptm.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.planet.Planet;
import old.tnf.ptm.game.AbilityCommonConfig;
import old.tnf.ptm.game.Faction;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.item.ItemManager;
import old.tnf.ptm.game.item.PtmItem;

public class Teleport implements ShipAbility {
    public static final int MAX_RADIUS = 4;
    public static final String TEX_PATH = "smallGameObjects/teleportBlip";
    private final Vector2 myNewPos;
    private final Config myConfig;
    private boolean myShouldTeleport;
    private float myAngle;

    public Teleport(Config config) {
        myConfig = config;
        myNewPos = new Vector2();
    }

    @Override
    public boolean update(PtmGame game, PtmShip owner, boolean tryToUse) {
        myShouldTeleport = false;
        if (!tryToUse) {
            return false;
        }
        Vector2 pos = owner.getPosition();
        Faction faction = owner.getPilot().getFaction();
        PtmShip ne = game.getFactionMan().getNearestEnemy(game, MAX_RADIUS, faction, pos);
        if (ne == null) {
            return false;
        }
        Vector2 nePos = ne.getPosition();
        Planet np = game.getPlanetMan().getNearestPlanet();
        if (np.isNearGround(nePos)) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            myNewPos.set(pos);
            myNewPos.sub(nePos);
            myAngle = myConfig.angle * PtmMath.rnd(.5f, 1) * PtmMath.toInt(PtmMath.test(.5f));
            PtmMath.rotate(myNewPos, myAngle);
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
    public void maybeTeleport(PtmGame game, PtmShip owner) {
        if (!myShouldTeleport) {
            return;
        }

        TextureAtlas.AtlasRegion tex = game.getTexMan().getTexture(TEX_PATH);
        float blipSz = owner.getHull().config.getApproxRadius() * 3;
        game.getPartMan().blip(game, owner.getPosition(), PtmMath.rnd(180), blipSz, 1, Vector2.Zero, tex);
        game.getPartMan().blip(game, myNewPos, PtmMath.rnd(180), blipSz, 1, Vector2.Zero, tex);

        float newAngle = owner.getAngle() + myAngle;
        Vector2 newSpd = PtmMath.getVec(owner.getSpd());
        PtmMath.rotate(newSpd, myAngle);

        Body body = owner.getHull().getBody();
        body.setTransform(myNewPos, newAngle * PtmMath.degRad);
        body.setLinearVelocity(newSpd);

        PtmMath.free(newSpd);
    }

    public static class Config implements AbilityConfig {
        private final float angle;
        private final PtmItem chargeExample;
        private final float rechargeTime;
        private final AbilityCommonConfig cc;

        public Config(float angle, PtmItem chargeExample, float rechargeTime, AbilityCommonConfig cc) {
            this.angle = angle;
            this.chargeExample = chargeExample;
            this.rechargeTime = rechargeTime;
            this.cc = cc;
        }

        public static AbilityConfig load(JsonValue abNode, ItemManager itemManager, AbilityCommonConfig cc) {
            float angle = abNode.getFloat("angle");
            PtmItem chargeExample = itemManager.getExample("teleportCharge");
            float rechargeTime = abNode.getFloat("rechargeTime");
            return new Config(angle, chargeExample, rechargeTime, cc);
        }

        public ShipAbility build() {
            return new Teleport(this);
        }

        @Override
        public PtmItem getChargeExample() {
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
    }
}
