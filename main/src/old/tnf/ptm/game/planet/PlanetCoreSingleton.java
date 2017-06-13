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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmColor;
import old.tnf.ptm.game.GameDrawer;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.PtmCam;

public class PlanetCoreSingleton {
    private final TextureAtlas.AtlasRegion myTex;

    public PlanetCoreSingleton(TextureManager textureManager) {
        myTex = textureManager.getTexture("planetStarCommons/planetCore");
    }

    public void draw(PtmGame game, GameDrawer drawer) {
        PtmCam cam = game.getCam();
        Vector2 camPos = cam.getPos();
        Planet p = game.getPlanetMan().getNearestPlanet();
        Vector2 pPos = p.getPos();
        float toCamLen = camPos.dst(pPos);
        float vd = cam.getViewDist();
        float gh = p.getMinGroundHeight();
        if (toCamLen < gh + vd) {
            float sz = gh;
            drawer.draw(myTex, sz * 2, sz * 2, sz, sz, pPos.x, pPos.y, p.getAngle(), PtmColor.WHITE);
        }
    }
}
