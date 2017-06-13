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
package com.tnf.ptm.handler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.tnf.ptm.entities.ship.PtmShip;
import com.tnf.ptm.handler.input.Pilot;
import com.tnf.ptm.entities.projectile.Projectile;
import com.tnf.ptm.entities.Faction;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.PtmObject;

import java.util.List;

public class FactionManager {

    private final MyRayBack myRayBack;

    public FactionManager() {
        myRayBack = new MyRayBack();
    }

    /**
     * Finds the nearest Enemy @{link PtmShip} for the given ship
     *
     * @param game the game object
     * @param ship the ship to find enemies for
     * @return the nearest Enemy ship
     */
    public PtmShip getNearestEnemy(PtmGame game, PtmShip ship) {
        Pilot pilot = ship.getPilot();
        float detectionDist = pilot.getDetectionDist();
        if (detectionDist <= 0) {
            return null;
        }
        detectionDist += ship.getHull().config.getApproxRadius();
        Faction f = pilot.getFaction();
        return getNearestEnemy(game, detectionDist, f, ship.getPosition());
    }

    /**
     * Finds the nearest Enemy for target seeking projectiles
     *
     * @param game       the game object
     * @param projectile the target seeking projectile
     * @return the nearest Enemy ship
     */
    public PtmShip getNearestEnemy(PtmGame game, Projectile projectile) {
        return getNearestEnemy(game, game.getCam().getViewDist(), projectile.getFaction(), projectile.getPosition());
    }

    /**
     * Finds the nearest Enemy @{link PtmShip}
     *
     * @param game          the game object
     * @param detectionDist the maximum distance allowed for detection
     * @param faction       the faction of the entity
     * @param position      the position of the entity
     * @return the nearest Enemy ship
     */
    public PtmShip getNearestEnemy(PtmGame game, float detectionDist, Faction faction, Vector2 position) {
        PtmShip nearestEnemyShip = null;
        float minimumDistance = detectionDist;
        List<PtmObject> objects = game.getObjMan().getObjs();
        for (int i = 0, objectsSize = objects.size(); i < objectsSize; i++) {
            PtmObject ptmObject = objects.get(i);
            if (!(ptmObject instanceof PtmShip)) {
                continue;
            }
            PtmShip potentialEnemyShip = (PtmShip) ptmObject;
            if (!areEnemies(faction, potentialEnemyShip.getPilot().getFaction())) {
                continue;
            }
            float distance = potentialEnemyShip.getPosition().dst(position) - potentialEnemyShip.getHull().config.getApproxRadius();
            if (minimumDistance < distance) {
                continue;
            }
            minimumDistance = distance;
            nearestEnemyShip = potentialEnemyShip;
        }
        return nearestEnemyShip;
    }

    private boolean hasObstacles(PtmGame game, PtmShip shipFrom, PtmShip shipTo) {
        myRayBack.shipFrom = shipFrom;
        myRayBack.shipTo = shipTo;
        myRayBack.hasObstacle = false;
        game.getObjMan().getWorld().rayCast(myRayBack, shipFrom.getPosition(), shipTo.getPosition());
        return myRayBack.hasObstacle;
    }

    public boolean areEnemies(PtmShip s1, PtmShip s2) {
        Faction f1 = s1.getPilot().getFaction();
        Faction f2 = s2.getPilot().getFaction();
        return areEnemies(f1, f2);
    }

    public boolean areEnemies(Faction f1, Faction f2) {
        return f1 != null && f2 != null && f1 != f2;
    }

    private static class MyRayBack implements RayCastCallback {
        public PtmShip shipFrom;
        public PtmShip shipTo;
        public boolean hasObstacle;

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            PtmObject o = (PtmObject) fixture.getBody().getUserData();
            if (o == shipFrom || o == shipTo) {
                return -1;
            }
            hasObstacle = true;
            return 0;
        }
    }
}
