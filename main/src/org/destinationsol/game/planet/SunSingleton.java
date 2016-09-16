/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.TextureManager;
import org.destinationsol.common.ManiColor;
import org.destinationsol.common.ManiMath;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.GameDrawer;
import org.destinationsol.game.ManiGame;
import org.destinationsol.game.SolObject;

public class SunSingleton {
  private static final float SUN_DMG = 4f;
  public static final float SUN_HOT_RAD = .75f * Const.SUN_RADIUS;
  public static final float GRAV_CONST = 2000;

  private final TextureAtlas.AtlasRegion myGradTex;
  private final TextureAtlas.AtlasRegion myWhiteTex;
  private final Color myGradTint;
  private final Color myFillTint;

  public SunSingleton(TextureManager textureManager) {
    myGradTex = textureManager.getTex("planetStarCommons/grad", null);
    myWhiteTex = textureManager.getTex("planetStarCommons/whiteTex", null);
    myGradTint = ManiColor.col(1, 1);
    myFillTint = ManiColor.col(1, 1);
  }


  public void draw(ManiGame game, GameDrawer drawer) {
    Vector2 camPos = game.getCam().getPos();
    SolSystem sys = game.getPlanetMan().getNearestSystem(camPos);
    Vector2 toCam = ManiMath.getVec(camPos);
    toCam.sub(sys.getPos());
    float toCamLen = toCam.len();
    if (toCamLen < Const.SUN_RADIUS) {
      float closeness = 1 - toCamLen / Const.SUN_RADIUS;
      myGradTint.a = ManiMath.clamp(closeness * 4, 0, 1);
      myFillTint.a = ManiMath.clamp((closeness - .25f) * 4, 0, 1);

      float sz = 2 * game.getCam().getViewDist();
      float gradAngle = ManiMath.angle(toCam) + 90;
      drawer.draw(myWhiteTex, sz*2, sz*2, sz, sz, camPos.x, camPos.y, 0, myFillTint);
      drawer.draw(myGradTex, sz*2, sz*2, sz, sz, camPos.x, camPos.y, gradAngle, myGradTint);
    }
    ManiMath.free(toCam);
  }

  public void doDmg(ManiGame game, SolObject obj, float toSys) {
    float dmg = SUN_DMG * game.getTimeStep();
    if (SUN_HOT_RAD < toSys) return;
    obj.receiveDmg(dmg, game, null, DmgType.FIRE);
  }
}
