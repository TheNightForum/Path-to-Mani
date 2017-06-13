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
package com.tnf.ptm.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tnf.ptm.common.*;
import com.tnf.ptm.entities.StarPort;
import com.tnf.ptm.entities.planet.Planet;
import com.tnf.ptm.screens.game.MainScreen;
import com.tnf.ptm.entities.ship.PtmShip;
import old.tnf.ptm.game.CamRotStrategy;
import old.tnf.ptm.game.MapDrawer;

public class PtmCam {
    public static final float CAM_ROT_SPD = 90f;
    private static final float VIEWPORT_HEIGHT = 5f;
    private static final float MAX_ZOOM_SPD = 5f;
    private static final float MED_ZOOM_SPD = 3f;
    private static final float ZOOM_CHG_SPD = .1f;
    private static final float MOVE_SPD = 3f;
    private static final float MAX_SHAKE = .07f;
    private static final float SHAKE_DAMP = MAX_SHAKE;
    private final CamRotStrategy myCamRotStrategy;
    private final OrthographicCamera myCam;
    private final Vector3 myTmpVec;

    private float myPrevHeroLife;
    private float myShake;
    private float myAngle;
    private float myZoom;
    private Vector2 myPos;

    public PtmCam(float r) {
        myCamRotStrategy = new CamRotStrategy.ToPlanet();
        myCam = new OrthographicCamera(VIEWPORT_HEIGHT * r, -VIEWPORT_HEIGHT);
        myZoom = calcZoom(Const.CAM_VIEW_DIST_GROUND);
        myPos = new Vector2();
        myTmpVec = new Vector3();
    }

    public Matrix4 getMtx() {
        return myCam.combined;
    }

    public void update(PtmGame game) {
        float life = 0;

        PtmShip hero = game.getHero();
        float ts = game.getTimeStep();
        if (hero == null) {
            StarPort.Transcendent trans = game.getTranscendentHero();
            if (trans == null) {
                if (DebugOptions.DIRECT_CAM_CONTROL) {
                    applyInput(game);
                }
            } else {
                myPos.set(trans.getPosition());
            }
        } else {
            Vector2 heroPos = hero.getHull().getBody().getWorldCenter();
            if (myZoom * VIEWPORT_HEIGHT < heroPos.dst(myPos)) {
                myPos.set(heroPos);
                game.getObjMan().resetDelays();
            } else {
                Vector2 moveDiff = PtmMath.getVec(hero.getSpd());
                moveDiff.scl(ts);
                myPos.add(moveDiff);
                PtmMath.free(moveDiff);
                float moveSpd = MOVE_SPD * ts;
                myPos.x = PtmMath.approach(myPos.x, heroPos.x, moveSpd);
                myPos.y = PtmMath.approach(myPos.y, heroPos.y, moveSpd);
            }
            life = hero.getLife();
        }

        if (life < myPrevHeroLife) {
            float shakeDiff = .1f * MAX_SHAKE * (myPrevHeroLife - life);
            myShake = PtmMath.approach(myShake, MAX_SHAKE, shakeDiff);
        } else {
            myShake = PtmMath.approach(myShake, 0, SHAKE_DAMP * ts);
        }
        myPrevHeroLife = life;

        Vector2 pos = PtmMath.fromAl(PtmMath.rnd(180), myShake);
        pos.add(myPos);
        applyPos(pos.x, pos.y);
        PtmMath.free(pos);

        float desiredAngle = myCamRotStrategy.getRotation(myPos, game);
        float rotSpd = CAM_ROT_SPD * ts;
        myAngle = PtmMath.approachAngle(myAngle, desiredAngle, rotSpd);
        applyAngle();

        updateMap(game);
    }

    public void updateMap(PtmGame game) {
        float ts = game.getTimeStep();
        float desiredViewDistance = getDesiredViewDistance(game);
        float desiredZoom = calcZoom(desiredViewDistance);
        myZoom = PtmMath.approach(myZoom, desiredZoom, ZOOM_CHG_SPD * ts);
        applyZoom(game.getMapDrawer());
        myCam.update();
    }

    private float getDesiredViewDistance(PtmGame game) {
        PtmShip hero = game.getHero();
        if (hero == null && game.getTranscendentHero() != null) { // hero is in transcendent state
            return Const.CAM_VIEW_DIST_SPACE;
        } else if (hero == null && game.getTranscendentHero() == null) {
            return Const.CAM_VIEW_DIST_GROUND;
        } else {
            float speed = hero.getSpd().len();
            float desiredViewDistance = Const.CAM_VIEW_DIST_SPACE;
            Planet nearestPlanet = game.getPlanetMan().getNearestPlanet(myPos);
            if (nearestPlanet.getFullHeight() < nearestPlanet.getPos().dst(myPos) && MAX_ZOOM_SPD < speed) {
                desiredViewDistance = Const.CAM_VIEW_DIST_JOURNEY;
            } else if (nearestPlanet.isNearGround(myPos) && speed < MED_ZOOM_SPD) {
                desiredViewDistance = Const.CAM_VIEW_DIST_GROUND;
            }
            desiredViewDistance += hero.getHull().config.getApproxRadius();
            return desiredViewDistance;
        }
    }

    private float calcZoom(float vd) {
        float h = vd * PtmMath.sqrt(2);
        return h / VIEWPORT_HEIGHT;
    }

    private void applyZoom(MapDrawer mapDrawer) {
        if (mapDrawer.isToggled()) {
            myCam.zoom = mapDrawer.getZoom();
            return;
        }
        if (DebugOptions.ZOOM_OVERRIDE != 0) {
            myCam.zoom = DebugOptions.ZOOM_OVERRIDE;
            return;
        }
        myCam.zoom = myZoom;
    }

    private void applyPos(float posX, float posY) {
        myCam.position.set(posX, posY, 0);
    }

    private void applyInput(PtmGame game) {
        MainScreen s = game.getScreens().mainScreen;
        boolean d = s.isDown();
        boolean u = s.isUp();
        boolean l = s.isLeft();
        boolean r = s.isRight();
        Vector2 v = PtmMath.getVec();
        if (l != r) {
            v.x = PtmMath.toInt(r);
        }
        if (d != u) {
            v.y = PtmMath.toInt(d);
        }
        v.scl(MOVE_SPD * game.getTimeStep());
        PtmMath.rotate(v, myAngle);
        myPos.add(v);
        PtmMath.free(v);
    }

    private void applyAngle() {
        Vector2 v = PtmMath.getVec(0, 1);
        PtmMath.rotate(v, myAngle);
        myCam.up.set(v.x, v.y, 0); // up is actually down, fcuk!!
        PtmMath.free(v);
    }

    public float getViewDist() {
        return getViewDist(myZoom);
    }

    public float getViewDist(float zoom) {
        float r = myCam.viewportWidth / myCam.viewportHeight;
        return .5f * VIEWPORT_HEIGHT * PtmMath.sqrt(1 + r * r) * zoom;
    }

    /**
     * @return angle of a vector pointing right on screen
     */
    public float getAngle() {
        return myAngle;
    }

    public Vector2 getPos() {
        return myPos;
    }

    public void setPos(Vector2 pos) {
        myPos.set(pos);
    }

    public void drawDebug(GameDrawer drawer) {
        float hOver2 = VIEWPORT_HEIGHT * myZoom / 2;
        float wOver2 = hOver2 * drawer.r;
        Vector2 dr = PtmMath.getVec(wOver2, hOver2);
        PtmMath.rotate(dr, myAngle);
        Vector2 dl = PtmMath.getVec(-wOver2, hOver2);
        PtmMath.rotate(dl, myAngle);
        Vector2 ul = PtmMath.getVec(dr);
        ul.scl(-1);
        Vector2 ur = PtmMath.getVec(dl);
        ur.scl(-1);
        dr.add(myPos);
        dl.add(myPos);
        ul.add(myPos);
        ur.add(myPos);

        float lw = getRealLineWidth();
        drawer.drawLine(drawer.debugWhiteTex, dr, dl, PtmColor.WHITE, lw, false);
        drawer.drawLine(drawer.debugWhiteTex, dl, ul, PtmColor.WHITE, lw, false);
        drawer.drawLine(drawer.debugWhiteTex, ul, ur, PtmColor.WHITE, lw, false);
        drawer.drawLine(drawer.debugWhiteTex, ur, dr, PtmColor.WHITE, lw, false);

        PtmMath.free(dr);
        PtmMath.free(dl);
        PtmMath.free(ul);
        PtmMath.free(ur);
    }

    public float getRealLineWidth() {
        return getViewHeight(myCam.zoom) / Gdx.graphics.getHeight();
    }

    public float getViewHeight() {
        return getViewHeight(myZoom);
    }

    public float getViewHeight(float zoom) {
        float r = -myCam.viewportHeight * zoom;
        if (r < 0) {
            throw new AssertionError("negative view height");
        }
        return r;
    }

    public float getViewWidth() {
        float r = myCam.viewportWidth * myZoom;
        if (r < 0) {
            throw new AssertionError("negative view width");
        }
        return r;
    }

    public float getRealZoom() {
        return myCam.zoom;
    }

    public boolean isVisible(Vector2 pos) {
        Vector2 rp = PtmMath.toRel(pos, myAngle, myPos);
        boolean res = isRelVisible(rp);
        PtmMath.free(rp);
        return res;
    }

    public boolean isRelVisible(Vector2 rp) {
        float wHalf = getViewWidth() / 2;
        if (wHalf < PtmMath.abs(rp.x)) {
            return false;
        }
        float hHalf = getViewHeight() / 2;
        if (hHalf < PtmMath.abs(rp.y)) {
            return false;
        }
        return true;
    }

    public float getDebugFontSize() {
        return .04f * getRealZoom();
    }

    public void screenToWorld(Vector2 pos) {
        myTmpVec.set(pos, 0);
        myCam.unproject(myTmpVec);
        pos.x = myTmpVec.x;
        pos.y = myTmpVec.y;
    }
}