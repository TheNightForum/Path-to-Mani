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
package com.tnf.ptm.entities.planet;

import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.entities.maze.MazeConfigs;
import com.tnf.ptm.common.DebugOptions;
import com.tnf.ptm.common.PtmNames;
import com.tnf.ptm.entities.maze.Maze;
import com.tnf.ptm.entities.maze.MazeConfig;

import java.util.ArrayList;
import java.util.List;

public class SystemsBuilder {
    public static final int SYS_COUNT = 2;
    public static final int MAZE_COUNT = SYS_COUNT * 2;
    public static final int PLANET_COUNT = 5;
    public static final float PLANET_SPD = .2f;
    private static final float GROUND_SPD = .2f;
    private static final float MAX_MAZE_RADIUS = 40f;
    private static final float MAZE_GAP = 10f;
    private static final float BELT_HALF_WIDTH = 20f;

    public List<PtmSystem> build(List<PtmSystem> systems, List<Planet> planets, ArrayList<SystemBelt> belts,
                                 PlanetConfigs planetConfigs,
                                 MazeConfigs mazeConfigs, ArrayList<Maze> mazes, SysConfigs sysConfigs, PtmNames names) {
        int sysLeft = SYS_COUNT;
        int mazesLeft = MAZE_COUNT;
        while (sysLeft > 0 || mazesLeft > 0) {
            boolean createSys = sysLeft > 0;
            if (createSys && mazesLeft > 0 && !systems.isEmpty()) {
                createSys = PtmMath.test(.5f);
            }
            if (createSys) {
                List<Float> ghs = generatePlanetGhs();
                float sysRadius = calcSysRadius(ghs);
                Vector2 pos = getBodyPos(systems, mazes, sysRadius);
                PtmSystem s = createSystem(ghs, pos, planets, belts, planetConfigs, sysRadius, sysConfigs, names, systems.isEmpty());
                systems.add(s);
                sysLeft--;
            } else {
                MazeConfig mc = PtmMath.elemRnd(mazeConfigs.configs);
                float mazeRadius = PtmMath.rnd(.7f, 1) * MAX_MAZE_RADIUS;
                Vector2 pos = getBodyPos(systems, mazes, mazeRadius + MAZE_GAP);
                Maze m = new Maze(mc, pos, mazeRadius);
                mazes.add(m);
                mazesLeft--;
            }
        }
        return systems;
    }

    private List<Float> generatePlanetGhs() {
        ArrayList<Float> res = new ArrayList<Float>();
        boolean beltCreated = false;
        for (int i = 0; i < PLANET_COUNT; i++) {
            boolean createBelt = !beltCreated && 0 < i && i < .5f * PLANET_COUNT && PtmMath.test(.6f);
            float gh;
            if (!createBelt) {
                gh = PtmMath.rnd(.5f, 1) * Const.MAX_GROUND_HEIGHT;
            } else {
                gh = -BELT_HALF_WIDTH;
                beltCreated = true;
            }
            res.add(gh);
        }
        return res;
    }

    private float calcSysRadius(List<Float> ghs) {
        float r = 0;
        r += Const.SUN_RADIUS;
        for (Float gh : ghs) {
            r += Const.PLANET_GAP;
            if (gh > 0) {
                r += Const.ATM_HEIGHT;
                r += gh;
                r += gh;
                r += Const.ATM_HEIGHT;
            } else {
                r -= gh;
                r -= gh;
            }
            r += Const.PLANET_GAP;
        }
        return r;
    }

    private Vector2 getBodyPos(List<PtmSystem> systems, ArrayList<Maze> mazes, float bodyRadius) {
        Vector2 res = new Vector2();
        float dist = 0;
        while (true) {
            for (int i = 0; i < 20; i++) {
                float angle = PtmMath.rnd(180);
                PtmMath.fromAl(res, angle, dist);
                boolean good = true;
                for (PtmSystem system : systems) {
                    if (system.getPos().dst(res) < system.getRadius() + bodyRadius) {
                        good = false;
                        break;
                    }
                }
                for (Maze maze : mazes) {
                    if (maze.getPos().dst(res) < maze.getRadius() + bodyRadius) {
                        good = false;
                        break;
                    }
                }
                if (good) {
                    return res;
                }
            }
            dist += Const.SUN_RADIUS;
        }
    }

    private PtmSystem createSystem(List<Float> ghs, Vector2 sysPos, List<Planet> planets, ArrayList<SystemBelt> belts,
                                   PlanetConfigs planetConfigs,
                                   float sysRadius, SysConfigs sysConfigs, PtmNames names, boolean firstSys) {
        boolean hard = !firstSys;
        String st = DebugOptions.FORCE_SYSTEM_TYPE;
        SysConfig sysConfig;
        if (st.isEmpty()) {
            sysConfig = sysConfigs.getRandomCfg(hard);
        } else {
            sysConfig = sysConfigs.getConfig(st);
        }
        String name = firstSys ? PtmMath.elemRnd(names.systems) : "Sol"; //hack
        PtmSystem s = new PtmSystem(sysPos, sysConfig, name, sysRadius);
        float planetDist = Const.SUN_RADIUS;
        for (int idx = 0, sz = ghs.size(); idx < sz; idx++) {
            Float gh = ghs.get(idx);
            float reserved;
            if (gh > 0) {
                reserved = Const.PLANET_GAP + Const.ATM_HEIGHT + gh;
            } else {
                reserved = Const.PLANET_GAP - gh;
            }
            planetDist += reserved;
            if (gh > 0) {
                String pt = DebugOptions.FORCE_PLANET_TYPE;
                PlanetConfig planetConfig;
                if (pt.isEmpty()) {
                    boolean inner = planetDist < sysRadius / 2;
                    planetConfig = planetConfigs.getRandom(!inner && !hard, inner && hard);
                } else {
                    planetConfig = planetConfigs.getConfig(pt);
                }
                Planet p = createPlanet(planetDist, s, gh, planetConfig, names);
                planets.add(p);
                s.getPlanets().add(p);
            } else {
                SysConfig beltConfig = sysConfigs.getRandomBelt(hard);
                SystemBelt belt = new SystemBelt(-gh, planetDist, s, beltConfig);
                belts.add(belt);
                s.addBelt(belt);
            }
            planetDist += reserved;
        }
        if (PtmMath.abs(sysRadius - planetDist) > .1f) {
            throw new AssertionError(sysRadius + " " + planetDist);
        }
        return s;
    }

    private Planet createPlanet(float planetDist, PtmSystem s, float groundHeight, PlanetConfig planetConfig,
                                PtmNames names) {
        float toSysRotSpd = PtmMath.arcToAngle(PLANET_SPD, planetDist) * PtmMath.toInt(PtmMath.test(.5f));
        float rotSpd = PtmMath.arcToAngle(GROUND_SPD, groundHeight) * PtmMath.toInt(PtmMath.test(.5f));
        String name = PtmMath.elemRnd(names.planets);
        return new Planet(s, PtmMath.rnd(180), planetDist, PtmMath.rnd(180), toSysRotSpd, rotSpd, groundHeight, false, planetConfig, name);
    }

}
