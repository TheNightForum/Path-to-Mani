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

package com.pathtomani.game.projectile;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.pathtomani.TextureManager;
import com.pathtomani.common.ManiMath;
import com.pathtomani.files.FileManager;
import com.pathtomani.game.DmgType;
import com.pathtomani.game.GameColors;
import com.pathtomani.game.particle.EffectConfig;
import com.pathtomani.game.particle.EffectTypes;
import com.pathtomani.game.sound.ManiSound;
import com.pathtomani.game.sound.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class ProjectileConfigs {

  private final Map<String, ProjectileConfig> myConfigs;

  public ProjectileConfigs(TextureManager textureManager, SoundManager soundManager, EffectTypes effectTypes, GameColors cols) {
    myConfigs = new HashMap<String, ProjectileConfig>();
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("projectiles.json");
    JsonValue parsed = r.parse(configFile);
    for (JsonValue sh : parsed) {
      String texName = "smallGameObjs/projectiles/" + sh.getString("texName");
      TextureAtlas.AtlasRegion tex = textureManager.getTex(texName, configFile);
      float texSz = sh.getFloat("texSz");
      float spdLen = sh.getFloat("spdLen");
      float physSize = sh.getFloat("physSize", 0);
      boolean stretch = sh.getBoolean("stretch", false);
      DmgType dmgType = DmgType.forName(sh.getString("dmgType"));
      String collisionSoundPath = sh.getString("collisionSound", "");
      ManiSound collisionSound = collisionSoundPath.isEmpty() ? null : soundManager.getSound(collisionSoundPath, configFile);
      float lightSz = sh.getFloat("lightSz", 0);
      EffectConfig trailEffect = EffectConfig.load(sh.get("trailEffect"), effectTypes, textureManager, configFile, cols);
      EffectConfig bodyEffect = EffectConfig.load(sh.get("bodyEffect"), effectTypes, textureManager, configFile, cols);
      EffectConfig collisionEffect = EffectConfig.load(sh.get("collisionEffect"), effectTypes, textureManager, configFile, cols);
      EffectConfig collisionEffectBg = EffectConfig.load(sh.get("collisionEffectBg"), effectTypes, textureManager, configFile, cols);
      float guideRotSpd = sh.getFloat("guideRotSpd", 0);
      boolean zeroAbsSpd = sh.getBoolean("zeroAbsSpd", false);
      Vector2 origin = ManiMath.readV2(sh.getString("texOrig", "0 0"));
      float acc = sh.getFloat("acceleration", 0);
      String workSoundDir = sh.getString("workSound", "");
      ManiSound workSound = workSoundDir.isEmpty() ? null : soundManager.getLoopedSound(workSoundDir, configFile);
      boolean bodyless = sh.getBoolean("massless", false);
      float density = sh.getFloat("density", -1);
      float dmg = sh.getFloat("dmg");
      float emTime = sh.getFloat("emTime", 0);
      ProjectileConfig c = new ProjectileConfig(tex, texSz, spdLen, stretch, physSize, dmgType, collisionSound,
        lightSz, trailEffect, bodyEffect, collisionEffect, collisionEffectBg, zeroAbsSpd, origin, acc, workSound, bodyless, density, guideRotSpd, dmg, emTime);
      myConfigs.put(sh.name, c);
    }
  }

  public ProjectileConfig find(String name) {
    return myConfigs.get(name);
  }
}
