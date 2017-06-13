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
package old.tnf.ptm.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.TextureManager;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.DmgType;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.GameDrawer;

public class SunSingleton {
    public static final float SUN_HOT_RAD = .75f * Const.SUN_RADIUS;
    public static final float GRAV_CONST = 2000;
    private static final float SUN_DMG = 4f;
    private final TextureAtlas.AtlasRegion myGradTex;
    private final TextureAtlas.AtlasRegion myWhiteTex;
    private final Color myGradTint;
    private final Color myFillTint;

    public SunSingleton(TextureManager textureManager) {
        myGradTex = textureManager.getTexture("planetStarCommons/grad");
        myWhiteTex = textureManager.getTexture("planetStarCommons/whiteTex");
        myGradTint = PtmColor.col(1, 1);
        myFillTint = PtmColor.col(1, 1);
    }

    public void draw(PtmGame game, GameDrawer drawer) {
        Vector2 camPos = game.getCam().getPos();
        PtmSystem sys = game.getPlanetMan().getNearestSystem(camPos);
        Vector2 toCam = PtmMath.getVec(camPos);
        toCam.sub(sys.getPos());
        float toCamLen = toCam.len();
        if (toCamLen < Const.SUN_RADIUS) {
            float closeness = 1 - toCamLen / Const.SUN_RADIUS;
            myGradTint.a = PtmMath.clamp(closeness * 4, 0, 1);
            myFillTint.a = PtmMath.clamp((closeness - .25f) * 4, 0, 1);

            float sz = 2 * game.getCam().getViewDist();
            float gradAngle = PtmMath.angle(toCam) + 90;
            drawer.draw(myWhiteTex, sz * 2, sz * 2, sz, sz, camPos.x, camPos.y, 0, myFillTint);
            drawer.draw(myGradTex, sz * 2, sz * 2, sz, sz, camPos.x, camPos.y, gradAngle, myGradTint);
        }
        PtmMath.free(toCam);
    }

    public void doDmg(PtmGame game, PtmObject obj, float toSys) {
        float dmg = SUN_DMG * game.getTimeStep();
        if (SUN_HOT_RAD < toSys) {
            return;
        }
        obj.receiveDmg(dmg, game, null, DmgType.FIRE);
    }
}
