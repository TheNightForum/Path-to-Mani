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
package old.tnf.ptm.game.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import old.tnf.ptm.game.DmgType;
import old.tnf.ptm.game.FarObj;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.dra.Dra;

import java.util.ArrayList;
import java.util.List;

public class LightObject implements PtmObject {

    private final LightSrc myLightSrc;
    private final ArrayList<Dra> myDras;
    private final Vector2 myPos;

    // consumes pos
    public LightObject(PtmGame game, float sz, boolean hasHalo, float intensity, Vector2 pos, float fadeTime, Color col) {
        myPos = pos;
        myLightSrc = new LightSrc(game, sz, hasHalo, intensity, new Vector2(), col);
        myLightSrc.setFadeTime(fadeTime);
        myLightSrc.setWorking();
        myDras = new ArrayList<Dra>();
        myLightSrc.collectDras(myDras);
    }

    @Override
    public void update(PtmGame game) {
        myLightSrc.update(false, 0, game);
    }

    @Override
    public boolean shouldBeRemoved(PtmGame game) {
        return myLightSrc.isFinished();
    }

    @Override
    public void onRemove(PtmGame game) {
    }

    @Override
    public void receiveDmg(float dmg, PtmGame game, Vector2 pos, DmgType dmgType) {
    }

    @Override
    public boolean receivesGravity() {
        return false;
    }

    @Override
    public void receiveForce(Vector2 force, PtmGame game, boolean acc) {
    }

    @Override
    public Vector2 getPosition() {
        return myPos;
    }

    @Override
    public FarObj toFarObj() {
        return null;
    }

    @Override
    public List<Dra> getDras() {
        return myDras;
    }

    @Override
    public float getAngle() {
        return 0;
    }

    @Override
    public Vector2 getSpd() {
        return null;
    }

    @Override
    public void handleContact(PtmObject other, ContactImpulse impulse, boolean isA, float absImpulse,
                              PtmGame game, Vector2 collPos) {
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public Boolean isMetal() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
