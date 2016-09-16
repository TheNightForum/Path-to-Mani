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

package com.pathtomani.game.farBg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.pathtomani.common.Const;
import com.pathtomani.effects.ManiColor;
import com.pathtomani.effects.ManiColorUtil;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.GameDrawer;
import com.pathtomani.game.ManiGame;
import com.pathtomani.entities.planet.Planet;
import com.pathtomani.effects.TextureManager;
import com.pathtomani.game.ManiCam;

import java.util.ArrayList;

public class FarBackgroundManagerOld {

  private final TextureAtlas.AtlasRegion myNebTex;
  private final ArrayList<FarBgStar> myStars;
  private final float myNebAngle;
  private final Color myNebTint;

  public FarBackgroundManagerOld(TextureManager textureManager) {
    myNebTex = textureManager.getTex("farBgBig/nebulae2", ManiMath.test(.5f), null);
    myNebAngle = ManiMath.rnd(180);
    myStars = new ArrayList<FarBgStar>();
    for (int i = 0; i < 400; i++) {
      FarBgStar star = new FarBgStar(textureManager);
      myStars.add(star);
    }
    myNebTint = ManiColor.col(.5f, 1);
  }

  public void draw(GameDrawer drawer, ManiCam cam, ManiGame game) {
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = cam.getPos();
    float nebPerc = (camPos.dst(np.getPos()) - np.getGroundHeight()) / (4 * Const.ATM_HEIGHT);
    nebPerc = ManiMath.clamp(nebPerc, 0, 1);
    myNebTint.a = nebPerc;

    float vd = cam.getViewDist();
    drawer.draw(myNebTex, vd * 2, vd * 2, vd, vd, camPos.x, camPos.y, myNebAngle, myNebTint);
    for (int i = 0, myStarsSize = myStars.size(); i < myStarsSize; i++) {
      FarBgStar star = myStars.get(i);
      star.draw(drawer, vd, camPos, cam.getAngle());
    }
  }

  private static class FarBgStar {

    private final Vector2 myShiftPerc;
    private final TextureAtlas.AtlasRegion myTex;
    private final float mySzPerc;
    private final Color myTint;
    private final Vector2 myPos;

    private FarBgStar(TextureManager textureManager) {
      myShiftPerc = new Vector2(ManiMath.rnd(1), ManiMath.rnd(1));
      myPos = new Vector2();
      boolean small = ManiMath.test(.8f);
      myTex = textureManager.getTex("deco/bigStar", null);
      mySzPerc = (small ? .01f : .04f) * ManiMath.rnd(.5f, 1);
      myTint = new Color();
      ManiColorUtil.fromHSB(ManiMath.rnd(0, 1), .25f, 1, .7f, myTint);
    }

    public void draw(GameDrawer drawer, float vd, Vector2 camPos, float camAngle) {
      float sz = vd * mySzPerc;
      myPos.set(myShiftPerc).scl(vd).add(camPos);
      drawer.drawAdditive(myTex, sz, sz, sz /2, sz /2, myPos.x, myPos.y, camAngle, myTint);
    }
  }
}
