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

package com.tnf.ptm.handler.dra;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.FarObj;
import com.tnf.ptm.common.PtmObject;
import com.tnf.ptm.common.RemoveController;
import com.tnf.ptm.common.PtmGame;

import java.util.List;

public class FarDras implements FarObj {
    private final List<Dra> myDras;
    private final Vector2 myPos;
    private final Vector2 mySpd;
    private final RemoveController myRemoveController;
    private final float myRadius;
    private final boolean myHideOnPlanet;

    public FarDras(List<Dra> dras, Vector2 pos, Vector2 spd, RemoveController removeController,
                   boolean hideOnPlanet) {
        myDras = dras;
        myPos = pos;
        mySpd = spd;
        myRemoveController = removeController;
        myRadius = DraMan.radiusFromDras(myDras);
        myHideOnPlanet = hideOnPlanet;
    }

    @Override
    public boolean shouldBeRemoved(PtmGame game) {
        return myRemoveController != null && myRemoveController.shouldRemove(myPos);
    }

    @Override
    public PtmObject toObj(PtmGame game) {
        return new DrasObject(myDras, myPos, mySpd, myRemoveController, false, myHideOnPlanet);
    }

    @Override
    public void update(PtmGame game) {
    }

    @Override
    public float getRadius() {
        return myRadius;
    }

    @Override
    public Vector2 getPos() {
        return myPos;
    }

    @Override
    public String toDebugString() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    public List<Dra> getDras() {
        return myDras;
    }
}
