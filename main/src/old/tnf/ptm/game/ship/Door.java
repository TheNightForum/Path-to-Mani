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

package old.tnf.ptm.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import old.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.FactionManager;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.dra.Dra;
import old.tnf.ptm.game.dra.RectSprite;
import old.tnf.ptm.game.Faction;
import old.tnf.ptm.game.input.Pilot;

import java.util.ArrayList;
import java.util.List;

public class Door {
    public static final float SPD_LEN = .4f;
    public static final float SENSOR_DIST = 3f;
    public static final float DOOR_LEN = 1.1f;
    public static final float MAX_OPEN_AWAIT = DOOR_LEN / SPD_LEN;
    private final PrismaticJoint myJoint;
    private final RectSprite myS;
    private float myOpenAwait;

    public Door(PrismaticJoint joint, RectSprite s) {
        myJoint = joint;
        myS = s;
    }

    public void update(PtmGame game, PtmShip ship) {
        Vector2 doorPos = getBody().getPosition();
        boolean open = myOpenAwait <= 0 && shouldOpen(game, ship, doorPos);
        if (open) {
            myOpenAwait = MAX_OPEN_AWAIT;
            myJoint.setMotorSpeed(SPD_LEN);
            game.getSoundManager().play(game, game.getSpecialSounds().doorMove, doorPos, ship);
        } else if (myOpenAwait > 0) {
            myOpenAwait -= game.getTimeStep();
            if (myOpenAwait < 0) {
                myJoint.setMotorSpeed(-SPD_LEN);
                game.getSoundManager().play(game, game.getSpecialSounds().doorMove, doorPos, ship);
            }
        }

        Vector2 shipPos = ship.getPosition();
        float shipAngle = ship.getAngle();
        PtmMath.toRel(doorPos, myS.getRelPos(), shipAngle, shipPos);
    }

    private boolean shouldOpen(PtmGame game, PtmShip ship, Vector2 doorPos) {
        Faction faction = ship.getPilot().getFaction();
        FactionManager factionManager = game.getFactionMan();
        List<PtmObject> objs = game.getObjMan().getObjs();
        for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
            PtmObject o = objs.get(i);
            if (o == ship) {
                continue;
            }
            if (!(o instanceof PtmShip)) {
                continue;
            }
            PtmShip ship2 = (PtmShip) o;
            Pilot pilot2 = ship2.getPilot();
            if (!pilot2.isUp()) {
                continue;
            }
            if (factionManager.areEnemies(pilot2.getFaction(), faction)) {
                continue;
            }
            if (ship2.getPosition().dst(doorPos) < SENSOR_DIST) {
                return true;
            }
        }
        return false;
    }

    public void collectDras(ArrayList<Dra> dras) {
        dras.add(myS);
    }

    public Body getBody() {
        return myJoint.getBodyB();
    }

    public void onRemove(PtmGame game) {
        World w = game.getObjMan().getWorld();
        Body doorBody = getBody();
        w.destroyJoint(myJoint);
        w.destroyBody(doorBody);
    }
}
