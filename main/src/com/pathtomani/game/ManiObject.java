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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.pathtomani.common.Nullable;
import com.pathtomani.managers.dra.Dra;

import java.util.List;

public interface ManiObject {
  void update(ManiGame game);
  boolean shouldBeRemoved(ManiGame game);
  void onRemove(ManiGame game);
  void receiveDmg(float dmg, ManiGame game, @Nullable Vector2 pos, DmgType dmgType);
  boolean receivesGravity();
  void receiveForce(Vector2 force, ManiGame game, boolean acc);
  Vector2 getPosition();
  FarObj toFarObj();
  List<Dra> getDras();
  float getAngle();
  Vector2 getSpd();
  void handleContact(ManiObject other, ContactImpulse impulse, boolean isA, float absImpulse, ManiGame game,
                     Vector2 collPos);
  String toDebugString();
  Boolean isMetal();
  boolean hasBody();
}
