/*
 * Copyright 2016 BurntGameProductions
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

package com.pathtomani.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.pathtomani.common.ManiMath;
import com.pathtomani.game.dra.Dra;
import com.pathtomani.game.dra.FarDras;
import com.pathtomani.Const;
import com.pathtomani.common.DebugCol;
import com.pathtomani.common.ManiColor;
import com.pathtomani.game.dra.DraMan;
import com.pathtomani.game.ship.FarShip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectManager {
  private static final float MAX_RADIUS_RECALC_AWAIT = 1f;
  private final List<ManiObject> myObjs;
  private final List<ManiObject> myToRemove;
  private final List<ManiObject> myToAdd;
  private final List<FarObjData> myFarObjs;
  private final List<FarShip> myFarShips;
  private final List<StarPort.MyFar> myFarPorts;
  private final World myWorld;
  private final Box2DDebugRenderer myDr;
  private final HashMap<ManiObject, Float> myRadii;

  private float myFarEndDist;
  private float myFarBeginDist;
  private float myRadiusRecalcAwait;

  public ObjectManager(ManiContactListener contactListener, FactionManager factionManager) {
    myObjs = new ArrayList<ManiObject>();
    myToRemove = new ArrayList<ManiObject>();
    myToAdd = new ArrayList<ManiObject>();
    myFarObjs = new ArrayList<FarObjData>();
    myFarShips = new ArrayList<FarShip>();
    myFarPorts = new ArrayList<StarPort.MyFar>();
    myWorld = new World(new Vector2(0, 0), true);
    myWorld.setContactListener(contactListener);
    myWorld.setContactFilter(new ManiContactFilter(factionManager));
    myDr = new Box2DDebugRenderer();
    myRadii = new HashMap<ManiObject, Float>();
  }

  public boolean containsFarObj(FarObj fo) {
    for (int i = 0, myFarObjsSize = myFarObjs.size(); i < myFarObjsSize; i++) {
      FarObjData fod = myFarObjs.get(i);
      if (fod.fo == fo) return true;
    }
    return false;
  }

  public void update(ManiGame game) {
    addRemove(game);

    float ts = game.getTimeStep();
    myWorld.step(ts, 6, 2);

    ManiCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    myFarEndDist = 1.5f * cam.getViewDist();
    myFarBeginDist = 1.33f * myFarEndDist;

    boolean recalcRad = false;
    if (myRadiusRecalcAwait > 0) {
      myRadiusRecalcAwait -= ts;
    } else {
      myRadiusRecalcAwait = MAX_RADIUS_RECALC_AWAIT;
      recalcRad = true;
    }

    for (int i1 = 0, myObjsSize = myObjs.size(); i1 < myObjsSize; i1++) {
      ManiObject o = myObjs.get(i1);
      o.update(game);
      ManiMath.checkVectorsTaken(o);
      List<Dra> dras = o.getDras();
      for (int i = 0, drasSize = dras.size(); i < drasSize; i++) {
        Dra dra = dras.get(i);
        dra.update(game, o);
      }

      if (o.shouldBeRemoved(game)) {
        removeObjDelayed(o);
        continue;
      }
      if (isFar(o, camPos)) {
        FarObj fo = o.toFarObj();
        if (fo != null) addFarObjNow(fo);
        removeObjDelayed(o);
        continue;
      }
      if (recalcRad) recalcRadius(o);
    }

    for (Iterator<FarObjData> it = myFarObjs.iterator(); it.hasNext(); ) {
      FarObjData fod = it.next();
      FarObj fo = fod.fo;
      fo.update(game);
      ManiMath.checkVectorsTaken(fo);
      if (fo.shouldBeRemoved(game)) {
        removeFo(it, fo);
        continue;
      }
      if (isNear(fod, camPos, ts)) {
        ManiObject o = fo.toObj(game);
        // Ensure that StarPorts are added straight away so that we can see if they overlap
        if (o instanceof StarPort)
          addObjNow(game, o);
        else
          addObjDelayed(o);
        removeFo(it, fo);
      }
    }
    addRemove(game);
  }

  private void removeFo(Iterator<FarObjData> it, FarObj fo) {
    it.remove();
    if (fo instanceof FarShip) myFarShips.remove(fo);
    if (fo instanceof StarPort.MyFar) myFarPorts.remove(fo);
  }

  private void recalcRadius(ManiObject o) {
    float rad = DraMan.radiusFromDras(o.getDras());
    myRadii.put(o, rad);
  }

  public float getPresenceRadius(ManiObject o) {
    Float res = getRadius(o);
    return res + Const.MAX_MOVE_SPD * (MAX_RADIUS_RECALC_AWAIT - myRadiusRecalcAwait);
  }

  public Float getRadius(ManiObject o) {
    Float res = myRadii.get(o);
    if (res == null) throw new AssertionError("no radius for " + o);
    return res;
  }

  private void addRemove(ManiGame game) {
    for (int i = 0, myToRemoveSize = myToRemove.size(); i < myToRemoveSize; i++) {
      ManiObject o = myToRemove.get(i);
      removeObjNow(game, o);
    }
    myToRemove.clear();

    for (int i = 0, myToAddSize = myToAdd.size(); i < myToAddSize; i++) {
      ManiObject o = myToAdd.get(i);
      addObjNow(game, o);
    }
    myToAdd.clear();
  }

  private void removeObjNow(ManiGame game, ManiObject o) {
    myObjs.remove(o);
    myRadii.remove(o);
    o.onRemove(game);
    game.getDraMan().objRemoved(o);
  }

  public void addObjNow(ManiGame game, ManiObject o) {
    if (DebugOptions.ASSERTIONS && myObjs.contains(o)) throw new AssertionError();
    myObjs.add(o);
    recalcRadius(o);
    game.getDraMan().objAdded(o);
  }

  private boolean isNear(FarObjData fod, Vector2 camPos, float ts) {
    if (fod.delay > 0) {
      fod.delay -= ts;
      return false;
    }
    FarObj fo = fod.fo;
    float r = fo.getRadius() * fod.depth;
    float dst = fo.getPos().dst(camPos) - r;
    if (dst < myFarEndDist) return true;
    fod.delay = (dst - myFarEndDist) / (2 * Const.MAX_MOVE_SPD);
    return false;
  }

  private boolean isFar(ManiObject o, Vector2 camPos) {
    float r = getPresenceRadius(o);
    List<Dra> dras = o.getDras();
    if (dras != null && dras.size() > 0) r *= dras.get(0).getLevel().depth;
    float dst = o.getPosition().dst(camPos) - r;
    return myFarBeginDist < dst;
  }

  public void drawDebug(GameDrawer drawer, ManiGame game) {
    if (DebugOptions.DRAW_OBJ_BORDERS) {
      drawDebug0(drawer, game);
    }
    if (DebugOptions.OBJ_INFO) {
      drawDebugStrings(drawer, game);
    }

    if (DebugOptions.DRAW_PHYSIC_BORDERS) {
      drawer.end();
      myDr.render(myWorld, game.getCam().getMtx());
      drawer.begin();
    }
  }

  private void drawDebugStrings(GameDrawer drawer, ManiGame game) {
    float fontSize = game.getCam().getDebugFontSize();
    for (ManiObject o : myObjs) {
      Vector2 pos = o.getPosition();
      String ds = o.toDebugString();
      if (ds != null) drawer.drawString(ds, pos.x, pos.y, fontSize, true, ManiColor.W);
    }
    for (FarObjData fod : myFarObjs) {
      FarObj fo = fod.fo;
      Vector2 pos = fo.getPos();
      String ds = fo.toDebugString();
      if (ds != null) drawer.drawString(ds, pos.x, pos.y, fontSize, true, ManiColor.G);
    }
  }

  private void drawDebug0(GameDrawer drawer, ManiGame game) {
    ManiCam cam = game.getCam();
    float lineWidth = cam.getRealLineWidth();
    float vh = cam.getViewHeight();
    for (ManiObject o : myObjs) {
      Vector2 pos = o.getPosition();
      float r = getRadius(o);
      drawer.drawCircle(drawer.debugWhiteTex, pos, r, DebugCol.OBJ, lineWidth, vh);
      drawer.drawLine(drawer.debugWhiteTex, pos.x, pos.y, o.getAngle(), r, DebugCol.OBJ, lineWidth);
    }
    for (FarObjData fod : myFarObjs) {
      FarObj fo = fod.fo;
      drawer.drawCircle(drawer.debugWhiteTex, fo.getPos(), fo.getRadius(), DebugCol.OBJ_FAR, lineWidth, vh);
    }
    drawer.drawCircle(drawer.debugWhiteTex, cam.getPos(), myFarBeginDist, ManiColor.W, lineWidth, vh);
    drawer.drawCircle(drawer.debugWhiteTex, cam.getPos(), myFarEndDist, ManiColor.W, lineWidth, vh);
  }

  public List<ManiObject> getObjs() {
    return myObjs;
  }


  public void addObjDelayed(ManiObject p) {
    if (DebugOptions.ASSERTIONS && myToAdd.contains(p)) throw new AssertionError();
    myToAdd.add(p);
  }

  public void removeObjDelayed(ManiObject obj) {
    if (DebugOptions.ASSERTIONS && myToRemove.contains(obj)) throw new AssertionError();
    myToRemove.add(obj);
  }

  public World getWorld() {
    return myWorld;
  }

  public void resetDelays() {
    for (int i = 0, myFarObjsSize = myFarObjs.size(); i < myFarObjsSize; i++) {
      FarObjData data = myFarObjs.get(i);
      data.delay = 0;
    }

  }

  public List<FarObjData> getFarObjs() {
    return myFarObjs;
  }

  public void addFarObjNow(FarObj fo) {
    float depth = 1f;
    if (fo instanceof FarDras) {
      List<Dra> dras = ((FarDras)fo).getDras();
      if (dras != null && dras.size() > 0) depth = dras.get(0).getLevel().depth;
    }
    FarObjData fod = new FarObjData(fo, depth);
    myFarObjs.add(fod);
    if (fo instanceof FarShip) myFarShips.add((FarShip) fo);
    if (fo instanceof StarPort.MyFar) myFarPorts.add((StarPort.MyFar) fo);
  }

  public List<FarShip> getFarShips() {
    return myFarShips;
  }

  public List<StarPort.MyFar> getFarPorts() {
    return myFarPorts;
  }

  public void dispose() {
    myWorld.dispose();
  }
}
