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
package old.tnf.ptm.game;

import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.planet.Planet;
import old.tnf.ptm.game.planet.PtmSystem;

public interface CamRotStrategy {
    public float getRotation(Vector2 pos, PtmGame game);

    public static class Static implements CamRotStrategy {
        public float getRotation(Vector2 pos, PtmGame game) {
            return 0;
        }
    }

    public static class ToPlanet implements CamRotStrategy {

        public float getRotation(Vector2 pos, PtmGame game) {
            Planet np = game.getPlanetMan().getNearestPlanet();
            float fh = np.getFullHeight();
            Vector2 npPos = np.getPos();
            if (npPos.dst(pos) < fh) {
                return PtmMath.angle(pos, npPos, true) - 90;
            }
            PtmSystem sys = game.getPlanetMan().getNearestSystem(pos);
            Vector2 sysPos = sys.getPos();
            if (sysPos.dst(pos) < Const.SUN_RADIUS) {
                return PtmMath.angle(pos, sysPos, true) - 90;
            }
            return 0;
        }
    }
}
