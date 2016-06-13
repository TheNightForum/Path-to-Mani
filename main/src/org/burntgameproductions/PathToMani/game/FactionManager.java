

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import org.burntgameproductions.PathToMani.game.input.Pilot;
import org.burntgameproductions.PathToMani.game.projectile.Projectile;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;

import java.util.List;

public class FactionManager {

    private final MyRayBack myRayBack;

    public FactionManager() {
        myRayBack = new MyRayBack();
    }

    /**
     * Finds the nearest Enemy @{link ManiShip} for the given ship
     * @param game the game object
     * @param ship the ship to find enemies for
     * @return the nearest Enemy ship
     */
    public ManiShip getNearestEnemy(ManiGame game, ManiShip ship) {
        Pilot pilot = ship.getPilot();
        float detectionDist = pilot.getDetectionDist();
        if (detectionDist <= 0) return null;
        detectionDist += ship.getHull().config.getApproxRadius();
        Faction f = pilot.getFaction();
        return getNearestEnemy(game, detectionDist, f, ship.getPosition());
    }

    /**
     * Finds the nearest Enemy for target seeking projectiles
     * @param game  the game object
     * @param projectile  the target seeking projectile
     * @return the nearest Enemy ship
     */
    public ManiShip getNearestEnemy(ManiGame game, Projectile projectile) {
        return getNearestEnemy(game, game.getCam().getViewDist(), projectile.getFaction(), projectile.getPosition());
    }

    /**
     * Finds the nearest Enemy @{link ManiShip}
     * @param game the game object
     * @param detectionDist the maximum distance allowed for detection
     * @param faction the faction of the entity
     * @param position the position of the entity
     * @return the nearest Enemy ship
     */
    public ManiShip getNearestEnemy(ManiGame game, float detectionDist, Faction faction, Vector2 position) {
        ManiShip nearestEnemyShip = null;
        float minimumDistance = detectionDist;
        List<ManiObject> objects = game.getObjMan().getObjs();
        for (int i = 0, objectsSize = objects.size(); i < objectsSize; i++) {
            ManiObject solObject = objects.get(i);
            if (!(solObject instanceof ManiShip)) {
                continue;
            }
            ManiShip potentialEnemyShip = (ManiShip) solObject;
            if (!areEnemies(faction, potentialEnemyShip.getPilot().getFaction()))
                continue;
            float distance = potentialEnemyShip.getPosition().dst(position) - potentialEnemyShip.getHull().config.getApproxRadius();
            if (minimumDistance < distance){
                continue;
            }
            minimumDistance = distance;
            nearestEnemyShip = potentialEnemyShip;
        }
        return nearestEnemyShip;
    }

    private boolean hasObstacles(ManiGame game, ManiShip shipFrom, ManiShip shipTo) {
        myRayBack.shipFrom = shipFrom;
        myRayBack.shipTo = shipTo;
        myRayBack.hasObstacle = false;
        game.getObjMan().getWorld().rayCast(myRayBack, shipFrom.getPosition(), shipTo.getPosition());
        return myRayBack.hasObstacle;
    }

    public boolean areEnemies(ManiShip s1, ManiShip s2) {
        Faction f1 = s1.getPilot().getFaction();
        Faction f2 = s2.getPilot().getFaction();
        return areEnemies(f1, f2);
    }

    public boolean areEnemies(Faction f1, Faction f2) {
        return f1 != null && f2 != null && f1 != f2;
    }

    private static class MyRayBack implements RayCastCallback {
        public ManiShip shipFrom;
        public ManiShip shipTo;
        public boolean hasObstacle;

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            ManiObject o = (ManiObject) fixture.getBody().getUserData();
            if (o == shipFrom || o == shipTo) {
                return -1;
            }
            hasObstacle = true;
            return 0;
        }
    }
}