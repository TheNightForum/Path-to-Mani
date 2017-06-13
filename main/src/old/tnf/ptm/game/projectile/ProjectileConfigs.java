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
package old.tnf.ptm.game.projectile;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import old.tnf.ptm.TextureManager;
import old.tnf.ptm.assets.audio.OggSound;
import old.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.DmgType;
import old.tnf.ptm.assets.Assets;
import old.tnf.ptm.assets.json.Json;
import old.tnf.ptm.game.GameColors;
import old.tnf.ptm.game.particle.EffectConfig;
import old.tnf.ptm.game.particle.EffectTypes;
import old.tnf.ptm.game.sound.OggSoundManager;
import org.terasology.assets.ResourceUrn;

import java.util.HashMap;
import java.util.Map;

public class ProjectileConfigs {

    private final Map<String, ProjectileConfig> myConfigs;

    public ProjectileConfigs(TextureManager textureManager, OggSoundManager soundManager, EffectTypes effectTypes, GameColors cols) {
        myConfigs = new HashMap<>();

        Json json = Assets.getJson(new ResourceUrn("core:projectilesConfig"));
        JsonValue rootNode = json.getJsonValue();

        for (JsonValue sh : rootNode) {
            String texName = "smallGameObjects/projectiles/" + sh.getString("texName");
            TextureAtlas.AtlasRegion tex = textureManager.getTexture(texName);
            float texSz = sh.getFloat("texSz");
            float spdLen = sh.getFloat("spdLen");
            float physSize = sh.getFloat("physSize", 0);
            boolean stretch = sh.getBoolean("stretch", false);
            DmgType dmgType = DmgType.forName(sh.getString("dmgType"));
            String collisionSoundUrn = sh.getString("collisionSound", "");
            OggSound collisionSound = collisionSoundUrn.isEmpty() ? null : soundManager.getSound(collisionSoundUrn);
            float lightSz = sh.getFloat("lightSz", 0);
            EffectConfig trailEffect = EffectConfig.load(sh.get("trailEffect"), effectTypes, textureManager, cols);
            EffectConfig bodyEffect = EffectConfig.load(sh.get("bodyEffect"), effectTypes, textureManager, cols);
            EffectConfig collisionEffect = EffectConfig.load(sh.get("collisionEffect"), effectTypes, textureManager, cols);
            EffectConfig collisionEffectBg = EffectConfig.load(sh.get("collisionEffectBg"), effectTypes, textureManager, cols);
            float guideRotSpd = sh.getFloat("guideRotSpd", 0);
            boolean zeroAbsSpd = sh.getBoolean("zeroAbsSpd", false);
            Vector2 origin = PtmMath.readV2(sh.getString("texOrig", "0 0"));
            float acc = sh.getFloat("acceleration", 0);
            String workSoundUrn = sh.getString("workSound", "");
            OggSound workSound = workSoundUrn.isEmpty() ? null : soundManager.getSound(workSoundUrn);
            boolean bodyless = sh.getBoolean("massless", false);
            float density = sh.getFloat("density", -1);
            float dmg = sh.getFloat("dmg");
            float emTime = sh.getFloat("emTime", 0);
            ProjectileConfig c = new ProjectileConfig(tex, texSz, spdLen, stretch, physSize, dmgType, collisionSound,
                    lightSz, trailEffect, bodyEffect, collisionEffect, collisionEffectBg, zeroAbsSpd, origin, acc, workSound, bodyless, density, guideRotSpd, dmg, emTime);
            myConfigs.put(sh.name, c);
        }

        json.dispose();
    }

    public ProjectileConfig find(String name) {
        return myConfigs.get(name);
    }
}
