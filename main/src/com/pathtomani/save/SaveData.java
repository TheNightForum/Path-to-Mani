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

package com.pathtomani.save;

import com.pathtomani.game.FarObj;
import com.pathtomani.game.planet.Planet;
import com.pathtomani.game.planet.SolSystem;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
  public final List<FarObj> farObjs;
  public final List<SolSystem> systems;
  public final List<Planet> planets;

  public SaveData() {
    farObjs = new ArrayList<FarObj>();
    planets = new ArrayList<Planet>();
    systems = new ArrayList<SolSystem>();
  }
}
