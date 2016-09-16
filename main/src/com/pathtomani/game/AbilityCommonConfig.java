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

package com.pathtomani.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.pathtomani.game.particle.EffectConfig;
import com.pathtomani.game.sound.SolSound;
import com.pathtomani.game.sound.SoundManager;
import com.pathtomani.TextureManager;
import com.pathtomani.game.particle.EffectTypes;

public class AbilityCommonConfig {
  public final EffectConfig effect;
  public final SolSound activatedSound;

  public AbilityCommonConfig(EffectConfig effect, SolSound activatedSound) {
    this.effect = effect;
    this.activatedSound = activatedSound;
  }

  public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TextureManager textureManager, GameColors cols,
    FileHandle configFile, SoundManager soundManager)
  {
    EffectConfig ec = EffectConfig.load(node.get("effect"), types, textureManager, configFile, cols);
    SolSound activatedSound = soundManager.getSound(node.getString("activatedSound"), configFile);
    return new AbilityCommonConfig(ec, activatedSound);
  }
}