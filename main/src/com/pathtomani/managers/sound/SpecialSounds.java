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

package com.pathtomani.managers.sound;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.pathtomani.common.Const;
import com.pathtomani.managers.files.FileManager;
import com.pathtomani.game.DmgType;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.ManiObject;

public class SpecialSounds {

  public final ManiSound metalColl;
  public final ManiSound metalBulletHit;
  public final ManiSound metalEnergyHit;
  public final ManiSound rockColl;
  public final ManiSound rockBulletHit;
  public final ManiSound rockEnergyHit;
  public final ManiSound asteroidCrack;
  public final ManiSound shipExplosion;
  public final ManiSound burning;
  public final ManiSound forceBeaconWork;
  public final ManiSound doorMove;
  public final ManiSound abilityRecharged;
  public final ManiSound abilityRefused;
  public final ManiSound controlDisabled;
  public final ManiSound controlEnabled;
  public final ManiSound lootThrow;
  public final ManiSound transcendentCreated;
  public final ManiSound transcendentFinished;
  public final ManiSound transcendentMove;

  public SpecialSounds(SoundManager soundManager) {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("specialSounds.json");
    JsonValue node = r.parse(configFile);
    metalColl = soundManager.getSound(node.getString("metalCollision"), configFile);
    metalBulletHit = soundManager.getPitchedSound(node.getString("metalBulletHit"), configFile, 1.1f);
    metalEnergyHit = soundManager.getSound(node.getString("metalEnergyHit"), configFile);
    rockColl = soundManager.getSound(node.getString("rockCollision"), configFile);
    rockBulletHit = soundManager.getSound(node.getString("rockBulletHit"), configFile);
    rockEnergyHit = soundManager.getSound(node.getString("rockEnergyHit"), configFile);
    asteroidCrack = soundManager.getSound(node.getString("asteroidCrack"), configFile);
    shipExplosion = soundManager.getSound(node.getString("shipExplosion"), configFile);
    burning = soundManager.getLoopedSound(node.getString("burning"), configFile);
    forceBeaconWork = soundManager.getLoopedSound(node.getString("forceBeaconWork"), configFile);
    doorMove = soundManager.getSound(node.getString("doorMove"), configFile);
    abilityRecharged = soundManager.getSound(node.getString("abilityRecharged"), configFile);
    abilityRefused = soundManager.getLoopedSound(node.getString("abilityRefused"), configFile);
    controlDisabled = soundManager.getSound(node.getString("controlDisabled"), configFile);
    controlEnabled = soundManager.getSound(node.getString("controlEnabled"), configFile);
    lootThrow = soundManager.getSound(node.getString("lootThrow"), configFile);
    transcendentCreated = soundManager.getSound(node.getString("transcendentCreated"), configFile);
    transcendentFinished = soundManager.getSound(node.getString("transcendentFinished"), configFile);
    transcendentMove = soundManager.getLoopedSound(node.getString("transcendentMove"), configFile);
  }

  public ManiSound hitSound(boolean forMetal, DmgType dmgType) {
    if (dmgType == DmgType.ENERGY) {
      return forMetal ? metalEnergyHit : rockEnergyHit;
    }
    if (dmgType == DmgType.BULLET) {
      return forMetal ? metalBulletHit : rockBulletHit;
    }
    return null;
  }

  public void playHit(ManiGame game, ManiObject o, Vector2 pos, DmgType dmgType) {
    if (o == null) return;
    Boolean metal = o.isMetal();
    if (metal == null) return;
    ManiSound sound = hitSound(metal, dmgType);
    if (sound == null) return;
    game.getSoundMan().play(game, sound, pos, o);
  }

  public void playColl(ManiGame game, float absImpulse, ManiObject o, Vector2 pos) {
    if (o == null || absImpulse < .1f) return;
    Boolean metal = o.isMetal();
    if (metal == null) return;
    game.getSoundMan().play(game, metal ? metalColl : rockColl, pos, o, absImpulse * Const.IMPULSE_TO_COLL_VOL);
  }
}
