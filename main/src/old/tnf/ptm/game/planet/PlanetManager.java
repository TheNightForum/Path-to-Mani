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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import old.tnf.ptm.Const;
import old.tnf.ptm.TextureManager;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.files.HullConfigManager;
import old.tnf.ptm.game.*;
import old.tnf.ptm.game.item.ItemManager;
import old.tnf.ptm.game.maze.Maze;
import old.tnf.ptm.game.maze.MazeConfigs;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.game.ship.hulls.Hull;
import old.tnf.ptm.game.ship.hulls.HullConfig;
import old.tnf.ptm.game.PtmObject;

import java.util.ArrayList;
import java.util.List;

public class PlanetManager {

    private final ArrayList<PtmSystem> mySystems;
    private final ArrayList<Planet> myPlanets;
    private final ArrayList<SystemBelt> myBelts;
    private final FlatPlaceFinder myFlatPlaceFinder;
    private final PlanetConfigs myPlanetConfigs;
    private final MazeConfigs myMazeConfigs;
    private final ArrayList<Maze> myMazes;
    private final SunSingleton mySunSingleton;
    private final SysConfigs mySysConfigs;
    private final PlanetCoreSingleton myPlanetCore;
    private Planet myNearestPlanet;

    public PlanetManager(TextureManager textureManager, HullConfigManager hullConfigs, GameColors cols,
                         ItemManager itemManager) {
        myPlanetConfigs = new PlanetConfigs(textureManager, hullConfigs, cols, itemManager);
        mySysConfigs = new SysConfigs(textureManager, hullConfigs, itemManager);
        myMazeConfigs = new MazeConfigs(textureManager, hullConfigs, itemManager);

        mySystems = new ArrayList<>();
        myMazes = new ArrayList<>();
        myPlanets = new ArrayList<>();
        myBelts = new ArrayList<>();
        myFlatPlaceFinder = new FlatPlaceFinder();
        mySunSingleton = new SunSingleton(textureManager);
        myPlanetCore = new PlanetCoreSingleton(textureManager);
    }

    public void fill(PtmNames names) {
        new SystemsBuilder().build(mySystems, myPlanets, myBelts, myPlanetConfigs, myMazeConfigs, myMazes, mySysConfigs, names);
    }

    public void update(PtmGame game) {
        Vector2 camPos = game.getCam().getPos();
        for (Planet planet : myPlanets) {
            planet.update(game);
        }
        for (Maze maze : myMazes) {
            maze.update(game);
        }

        myNearestPlanet = getNearestPlanet(camPos);

        PtmSystem nearestSys = getNearestSystem(camPos);
        applyGrav(game, nearestSys);
    }

    public Planet getNearestPlanet(Vector2 pos) {
        float minDst = Float.MAX_VALUE;
        Planet res = null;
        for (Planet planet : myPlanets) {
            float dst = pos.dst(planet.getPos());
            if (dst < minDst) {
                minDst = dst;
                res = planet;
            }
        }
        return res;
    }

    private void applyGrav(PtmGame game, PtmSystem nearestSys) {
        float npGh = myNearestPlanet.getGroundHeight();
        float npFh = myNearestPlanet.getFullHeight();
        float npMinH = myNearestPlanet.getMinGroundHeight();
        Vector2 npPos = myNearestPlanet.getPos();
        Vector2 sysPos = nearestSys.getPos();
        float npGravConst = myNearestPlanet.getGravConst();

        List<PtmObject> objs = game.getObjMan().getObjs();
        for (PtmObject obj : objs) {
            if (!obj.receivesGravity()) {
                continue;
            }

            Vector2 objPos = obj.getPosition();
            float minDist;
            Vector2 srcPos;
            float gravConst;
            boolean onPlanet;
            float toNp = npPos.dst(objPos);
            float toSys = sysPos.dst(objPos);
            if (toNp < npFh) {
                if (recoverObj(obj, toNp, npMinH)) {
                    continue;
                }
                minDist = npGh;
                srcPos = npPos;
                gravConst = npGravConst;
                onPlanet = true;
            } else if (toSys < Const.SUN_RADIUS) {
                minDist = SunSingleton.SUN_HOT_RAD;
                srcPos = sysPos;
                gravConst = SunSingleton.GRAV_CONST;
                onPlanet = false;
            } else {
                continue;
            }

            Vector2 grav = PtmMath.getVec(srcPos);
            grav.sub(objPos);
            float len = grav.len();
            grav.nor();
            if (len < minDist) {
                len = minDist;
            }
            float g = gravConst / len / len;
            grav.scl(g);
            obj.receiveForce(grav, game, true);
            PtmMath.free(grav);
            if (!onPlanet) {
                mySunSingleton.doDmg(game, obj, toSys);
            }
        }

    }

    private boolean recoverObj(PtmObject obj, float toNp, float npMinH) {
        if (npMinH < toNp) {
            return false;
        }
        if (!(obj instanceof PtmShip)) {
            return false;
        }
        PtmShip ship = (PtmShip) obj;
        Hull hull = ship.getHull();
        if (hull.config.getType() == HullConfig.Type.STATION) {
            return false;
        }
        float fh = myNearestPlanet.getFullHeight();
        Vector2 npPos = myNearestPlanet.getPos();
        Vector2 toShip = PtmMath.distVec(npPos, ship.getPosition());
        float len = toShip.len();
        if (len == 0) {
            toShip.set(0, fh);
        } else {
            toShip.scl(fh / len);
        }
        toShip.add(npPos);
        Body body = hull.getBody();
        body.setTransform(toShip, 0);
        body.setLinearVelocity(Vector2.Zero);
        PtmMath.free(toShip);
        return true;
    }

    public Planet getNearestPlanet() {
        return myNearestPlanet;
    }

    public void drawDebug(GameDrawer drawer, PtmGame game) {
        if (DebugOptions.DRAW_PLANET_BORDERS) {
            PtmCam cam = game.getCam();
            float lineWidth = cam.getRealLineWidth();
            float vh = cam.getViewHeight();
            for (Planet p : myPlanets) {
                Vector2 pos = p.getPos();
                float angle = p.getAngle();
                float fh = p.getFullHeight();
                Color col = p == myNearestPlanet ? PtmColor.WHITE : PtmColor.G;
                drawer.drawCircle(drawer.debugWhiteTex, pos, p.getGroundHeight(), col, lineWidth, vh);
                drawer.drawCircle(drawer.debugWhiteTex, pos, fh, col, lineWidth, vh);
                drawer.drawLine(drawer.debugWhiteTex, pos.x, pos.y, angle, fh, col, lineWidth);
            }

        }
    }

    public ArrayList<Planet> getPlanets() {
        return myPlanets;
    }

    public ArrayList<SystemBelt> getBelts() {
        return myBelts;
    }

    public ArrayList<PtmSystem> getSystems() {
        return mySystems;
    }

    public Vector2 findFlatPlace(PtmGame game, Planet p, ConsumedAngles takenAngles,
                                 float objHalfWidth) {
        return myFlatPlaceFinder.find(game, p, takenAngles, objHalfWidth);
    }

    public ArrayList<Maze> getMazes() {
        return myMazes;
    }

    public PtmSystem getNearestSystem(Vector2 pos) {
        float minDst = Float.MAX_VALUE;
        PtmSystem res = null;
        for (PtmSystem system : mySystems) {
            float dst = pos.dst(system.getPos());
            if (dst < minDst) {
                minDst = dst;
                res = system;
            }
        }
        return res;
    }

    public Maze getNearestMaze(Vector2 pos) {
        float minDst = Float.MAX_VALUE;
        Maze res = null;
        for (Maze maze : myMazes) {
            float dst = pos.dst(maze.getPos());
            if (dst < minDst) {
                minDst = dst;
                res = maze;
            }
        }
        return res;
    }

    public void drawSunHack(PtmGame game, GameDrawer drawer) {
        mySunSingleton.draw(game, drawer);
    }

    public void drawPlanetCoreHack(PtmGame game, GameDrawer drawer) {
        myPlanetCore.draw(game, drawer);
    }
}
