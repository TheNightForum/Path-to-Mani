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

package old.tnf.ptm.game.farBg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmColorUtil;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.GameDrawer;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.planet.Planet;
import old.tnf.ptm.game.PtmCam;

import java.util.ArrayList;

public class FarBackgroundManagerOld {

    private final TextureAtlas.AtlasRegion myNebTex;
    private final ArrayList<FarBgStar> myStars;
    private final float myNebAngle;
    private final Color myNebTint;

    public FarBackgroundManagerOld(TextureManager textureManager) {
        TextureAtlas.AtlasRegion nebTex = textureManager.getTexture("farBgBig/nebulae2");
        if (PtmMath.test(.5f)) {
            nebTex = textureManager.getFlipped(nebTex);
        }
        myNebTex = nebTex;

        myNebAngle = PtmMath.rnd(180);
        myStars = new ArrayList<FarBgStar>();
        for (int i = 0; i < 400; i++) {
            FarBgStar star = new FarBgStar(textureManager);
            myStars.add(star);
        }
        myNebTint = PtmColor.col(.5f, 1);
    }

    public void draw(GameDrawer drawer, PtmCam cam, PtmGame game) {
        Planet np = game.getPlanetMan().getNearestPlanet();
        Vector2 camPos = cam.getPos();
        float nebPerc = (camPos.dst(np.getPos()) - np.getGroundHeight()) / (4 * Const.ATM_HEIGHT);
        nebPerc = PtmMath.clamp(nebPerc, 0, 1);
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
            myShiftPerc = new Vector2(PtmMath.rnd(1), PtmMath.rnd(1));
            myPos = new Vector2();
            boolean small = PtmMath.test(.8f);
            myTex = textureManager.getTexture("decorations/bigStar");
            mySzPerc = (small ? .01f : .04f) * PtmMath.rnd(.5f, 1);
            myTint = new Color();
            PtmColorUtil.fromHSB(PtmMath.rnd(0, 1), .25f, 1, .7f, myTint);
        }

        public void draw(GameDrawer drawer, float vd, Vector2 camPos, float camAngle) {
            float sz = vd * mySzPerc;
            myPos.set(myShiftPerc).scl(vd).add(camPos);
            drawer.drawAdditive(myTex, sz, sz, sz / 2, sz / 2, myPos.x, myPos.y, camAngle, myTint);
        }
    }
}
