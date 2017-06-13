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
package com.tnf.ptm.common;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.tnf.ptm.entities.projectile.Projectile;
import com.tnf.ptm.handler.FactionManager;

public class PtmContactFilter implements ContactFilter {
    private final FactionManager myFactionManager;

    public PtmContactFilter(FactionManager factionManager) {
        myFactionManager = factionManager;
    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        PtmObject oA = (PtmObject) fixtureA.getBody().getUserData();
        PtmObject oB = (PtmObject) fixtureB.getBody().getUserData();

        boolean aIsProj = oA instanceof Projectile;
        if (!aIsProj && !(oB instanceof Projectile)) {
            return true;
        }

        Projectile proj = (Projectile) (aIsProj ? oA : oB);
        PtmObject o = aIsProj ? oB : oA;
        Fixture f = aIsProj ? fixtureB : fixtureA;
        return proj.shouldCollide(o, f, myFactionManager);
    }
}
