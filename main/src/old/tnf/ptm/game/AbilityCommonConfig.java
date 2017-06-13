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
import old.tnf.ptm.TextureManager;
import old.tnf.ptm.assets.audio.PlayableSound;
import old.tnf.ptm.assets.audio.OggSound;
import old.tnf.ptm.game.particle.EffectConfig;
import old.tnf.ptm.game.particle.EffectTypes;
import old.tnf.ptm.game.sound.OggSoundManager;

public class AbilityCommonConfig {
    public final EffectConfig effect;
    public final PlayableSound activatedSound;

    public AbilityCommonConfig(EffectConfig effect, PlayableSound activatedSound) {
        this.effect = effect;
        this.activatedSound = activatedSound;
    }

    public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TextureManager textureManager,
                                           GameColors cols, OggSoundManager soundManager) {
        EffectConfig ec = EffectConfig.load(node.get("effect"), types, textureManager, cols);
        OggSound activatedSound = soundManager.getSound(node.getString("activatedSound"));
        return new AbilityCommonConfig(ec, activatedSound);
    }
}
