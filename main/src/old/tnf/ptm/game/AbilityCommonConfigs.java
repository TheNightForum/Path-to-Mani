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
package old.tnf.ptm.game;

import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.assets.json.Json;
import com.tnf.ptm.gfx.particle.EffectTypes;
import com.tnf.ptm.sound.OggSoundManager;
import org.terasology.assets.ResourceUrn;

public class AbilityCommonConfigs {
    public final AbilityCommonConfig teleport;
    public final AbilityCommonConfig emWave;
    public final AbilityCommonConfig unShield;
    public final AbilityCommonConfig knockBack;
    public final AbilityCommonConfig sloMo;

    public AbilityCommonConfigs(EffectTypes effectTypes, TextureManager textureManager, GameColors cols, OggSoundManager soundManager) {
        Json json = Assets.getJson(new ResourceUrn("core:abilitiesConfig"));
        JsonValue rootNode = json.getJsonValue();

        teleport = AbilityCommonConfig.load(rootNode.get("teleport"), effectTypes, textureManager, cols, soundManager);
        emWave = AbilityCommonConfig.load(rootNode.get("emWave"), effectTypes, textureManager, cols, soundManager);
        unShield = AbilityCommonConfig.load(rootNode.get("unShield"), effectTypes, textureManager, cols, soundManager);
        knockBack = AbilityCommonConfig.load(rootNode.get("knockBack"), effectTypes, textureManager, cols, soundManager);
        sloMo = AbilityCommonConfig.load(rootNode.get("sloMo"), effectTypes, textureManager, cols, soundManager);

        json.dispose();
    }
}
