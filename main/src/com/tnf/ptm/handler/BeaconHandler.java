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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.PtmGame;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.handler.ObjectManager;
import com.tnf.ptm.handler.dra.Dra;
import com.tnf.ptm.handler.dra.DraLevel;
import com.tnf.ptm.handler.dra.DrasObject;
import com.tnf.ptm.handler.dra.RectSprite;
import com.tnf.ptm.entities.planet.PlanetBind;
import com.tnf.ptm.entities.ship.FarShip;
import com.tnf.ptm.entities.ship.PtmShip;
import com.tnf.ptm.handler.dra.FarDras;
import com.tnf.ptm.handler.input.Pilot;

import java.util.ArrayList;
import java.util.List;

public class BeaconHandler {

    public static final float TEX_SZ = .5f;
    public static final float ROT_SPD = 30f;

    private final RectSprite myAttackSprite;
    private final RectSprite myFollowSprite;
    private final RectSprite myMoveSprite;
    private final Vector2 myTargetRelPos;

    private DrasObject myD;
    private FarDras myFarD;
    private Pilot myTargetPilot;
    private PtmShip myTarget;
    private FarShip myFarTarget;
    private Action myCurrAction;
    private PlanetBind myPlanetBind;
    private float myClickTime;
    private Vector2 mySpd;
    private boolean myInitialized;

    public BeaconHandler(TextureManager textureManager) {
        TextureAtlas.AtlasRegion attackTex = textureManager.getTexture("smallGameObjects/beaconAttack");
        myAttackSprite = new RectSprite(attackTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        TextureAtlas.AtlasRegion followTex = textureManager.getTexture("smallGameObjects/beaconFollow");
        myFollowSprite = new RectSprite(followTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        TextureAtlas.AtlasRegion moveTex = textureManager.getTexture("smallGameObjects/beaconMove");
        myMoveSprite = new RectSprite(moveTex, TEX_SZ, 0, 0, new Vector2(), DraLevel.PART_FG_0, 0, ROT_SPD, new Color(1, 1, 1, 0), true);
        myTargetRelPos = new Vector2();
        mySpd = new Vector2();
    }

    public void init(PtmGame game, Vector2 pos) {
        ArrayList<Dra> dras = new ArrayList<Dra>();
        dras.add(myAttackSprite);
        dras.add(myFollowSprite);
        dras.add(myMoveSprite);
        myD = new DrasObject(dras, new Vector2(pos), new Vector2(), null, false, false);
        game.getObjMan().addObjDelayed(myD);
        myInitialized = true;
    }

    public void update(PtmGame game) {
        if (!myInitialized) {
            return;
        }
        updateD(game);
        mySpd.set(0, 0);
        if (maybeUpdateTargetPos(game)) {
            return;
        }
        maybeUpdatePlanetPos(game);
    }

    private void maybeUpdatePlanetPos(PtmGame game) {
        Vector2 beaconPos = getPos0();
        if (myPlanetBind == null) {
            myPlanetBind = PlanetBind.tryBind(game, beaconPos, 0);
            return;
        }
        Vector2 vec = PtmMath.getVec();
        myPlanetBind.setDiff(vec, beaconPos, false);
        beaconPos.add(vec);
        PtmMath.free(vec);
        myPlanetBind.getPlanet().calcSpdAtPos(mySpd, beaconPos);
    }

    private boolean maybeUpdateTargetPos(PtmGame game) {
        updateTarget(game);
        if (myTargetPilot == null) {
            return false;
        }
        Vector2 beaconPos = getPos0();
        if (myTarget != null) {
            PtmMath.toWorld(beaconPos, myTargetRelPos, myTarget.getAngle(), myTarget.getPosition(), false);
            mySpd.set(myTarget.getSpd());
        } else {
            beaconPos.set(myFarTarget.getPos());
        }
        return true;
    }

    private void updateTarget(PtmGame game) {
        if (myTargetPilot == null) {
            return;
        }
        ObjectManager om = game.getObjMan();
        List<PtmObject> objs = om.getObjs();
        List<FarShip> farShips = om.getFarShips();
        if (myTarget != null) {
            if (objs.contains(myTarget)) {
                return;
            }
            myTarget = null;
            for (FarShip ship : farShips) {
                if (ship.getPilot() != myTargetPilot) {
                    continue;
                }
                myFarTarget = ship;
                return;
            }
            applyAction(Action.MOVE);
            return;
        }
        if (myFarTarget == null) {
            throw new AssertionError();
        }
        if (om.getFarShips().contains(myFarTarget)) {
            return;
        }
        myFarTarget = null;
        for (PtmObject o : objs) {
            if ((o instanceof PtmShip)) {
                PtmShip ship = (PtmShip) o;
                if (ship.getPilot() != myTargetPilot) {
                    continue;
                }
                myTarget = ship;
                return;
            }
        }
        applyAction(Action.MOVE);
    }

    private void updateD(PtmGame game) {
        ObjectManager om = game.getObjMan();
        List<PtmObject> objs = om.getObjs();
        List<FarObjData> farObjs = om.getFarObjs();

        if (myD != null) {
            if (objs.contains(myD)) {
                return;
            }
            myD = null;
            for (FarObjData fod : farObjs) {
                FarObj fo = fod.fo;
                if (!(fo instanceof FarDras)) {
                    continue;
                }
                List<Dra> dras = ((FarDras) fo).getDras();
                if (dras.size() != 3) {
                    continue;
                }
                Dra dra = dras.get(0);
                if (dra != myAttackSprite) {
                    continue;
                }
                myFarD = (FarDras) fo;
                return;
            }
            throw new AssertionError();
        }
        if (myFarD == null) {
            throw new AssertionError();
        }
        if (om.containsFarObj(myFarD)) {
            return;
        }
        myFarD = null;
        for (PtmObject o : objs) {
            if ((o instanceof DrasObject)) {
                List<Dra> dras = o.getDras();
                if (dras.size() != 3) {
                    continue;
                }
                Dra dra = dras.get(0);
                if (dra != myAttackSprite) {
                    continue;
                }
                myD = (DrasObject) o;
                return;
            }
        }
        throw new AssertionError();
    }

    public Action processMouse(PtmGame g, Vector2 pos, boolean clicked, boolean onMap) {
        Action action;
        Pilot targetPilot = findPilotInPos(g, pos, onMap, clicked);
        if (targetPilot != null) {
            boolean enemies = g.getFactionMan().areEnemies(targetPilot.getFaction(), g.getHero().getPilot().getFaction());
            if (enemies) {
                action = Action.ATTACK;
                if (clicked) {
                    myTargetRelPos.set(0, 0);
                }
            } else {
                action = Action.FOLLOW;
                if (clicked) {
                    if (myTarget == null) {
                        myTargetRelPos.set(0, 0);
                    } else {
                        PtmMath.toRel(pos, myTargetRelPos, myTarget.getAngle(), myTarget.getPosition());
                    }
                }
            }
        } else {
            action = Action.MOVE;
        }

        if (clicked) {
            applyAction(action);
            getPos0().set(pos);
            myClickTime = g.getTime();
        }
        return action;
    }

    private void applyAction(Action action) {
        myCurrAction = action;
        myAttackSprite.tint.a = myCurrAction == Action.ATTACK ? 1 : 0;
        myMoveSprite.tint.a = myCurrAction == Action.MOVE ? 1 : 0;
        myFollowSprite.tint.a = myCurrAction == Action.FOLLOW ? 1 : 0;
        myPlanetBind = null;
        if (myCurrAction == Action.MOVE) {
            myTargetPilot = null;
            myTarget = null;
            myFarTarget = null;
        }
    }

    private Pilot findPilotInPos(PtmGame g, Vector2 pos, boolean onMap, boolean clicked) {
        ObjectManager om = g.getObjMan();
        PtmShip h = g.getHero();
        float iconRad = onMap ? g.getMapDrawer().getIconRadius(g.getCam()) : 0;
        for (PtmObject o : om.getObjs()) {
            if (o == h || !(o instanceof PtmShip)) {
                continue;
            }
            PtmShip s = (PtmShip) o;
            Pilot pilot = s.getPilot();
            if (onMap && pilot.getMapHint() == null) {
                continue;
            }
            float dst = o.getPosition().dst(pos);
            float rad = iconRad == 0 ? s.getHull().config.getSize() : iconRad;
            if (dst < rad) {
                if (clicked) {
                    myTargetPilot = pilot;
                    myTarget = s;
                }
                return pilot;
            }
        }
        for (FarShip s : om.getFarShips()) {
            Pilot pilot = s.getPilot();
            if (onMap && pilot.getMapHint() == null) {
                continue;
            }
            float dst = s.getPos().dst(pos);
            float rad = iconRad == 0 ? s.getHullConfig().getApproxRadius() : iconRad;
            if (dst < rad) {
                if (clicked) {
                    myTargetPilot = pilot;
                    myFarTarget = s;
                }
                return pilot;
            }
        }
        return null;
    }

    public Vector2 getPos() {
        return getPos0();
    }

    // returns Vector itself
    private Vector2 getPos0() {
        return myD == null ? myFarD.getPos() : myD.getPosition();
    }

    public Action getCurrAction() {
        return myCurrAction;
    }

    public float getClickTime() {
        return myClickTime;
    }

    public Vector2 getSpd() {
        return mySpd;
    }

    public static enum Action {
        MOVE, ATTACK, FOLLOW
    }
}
