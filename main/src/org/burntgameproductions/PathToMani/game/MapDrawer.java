

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.maze.MazeBuilder;
import org.burntgameproductions.PathToMani.game.planet.*;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.ui.UiDrawer;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.common.SolColor;
import org.burntgameproductions.PathToMani.game.maze.Maze;
import org.burntgameproductions.PathToMani.game.ship.FarShip;

import java.util.ArrayList;
import java.util.List;

public class MapDrawer {
  public static final float MIN_ZOOM = 8f;
  public static final float MUL_FACTOR = 2f;
  public static final float MAX_ZOOM = 512f;
  public static final float ICON_RAD = .02f;
  public static final float STAR_NODE_SZ = .003f;
  private static final float MAX_SKULL_TIME = .75f;
  private static final float MAX_AREA_SKULL_TIME = 3;
  public static final float INNER_ICON_PERC = .6f;
  public static final float INNER_AREA_ICON_PERC = .7f;
  public static final float GRID_SZ = 40f;
  public static final String MAP_TEX_DIR = "mapObjs/";
  public static final float MIN_ICON_RAD_PX = 16f;

  private final TextureAtlas.AtlasRegion myAtmTex;
  private final TextureAtlas.AtlasRegion myPlanetTex;
  private final TextureAtlas.AtlasRegion myPlanetCoreTex;
  private final TextureAtlas.AtlasRegion myStarTex;
  private final TextureAtlas.AtlasRegion myMazeTex;
  private final TextureAtlas.AtlasRegion mySkullTex;
  private final TextureAtlas.AtlasRegion mySkullBigTex;
  private final TextureAtlas.AtlasRegion myStarPortTex;
  private final TextureAtlas.AtlasRegion myBeltTex;
  private final TextureAtlas.AtlasRegion myBeaconAttackTex;
  private final TextureAtlas.AtlasRegion myBeaconMoveTex;
  private final TextureAtlas.AtlasRegion myBeaconFollowTex;
  private final TextureAtlas.AtlasRegion myIconBg;
  private final TextureAtlas.AtlasRegion myWarnAreaBg;
  private final TextureAtlas.AtlasRegion myWhiteTex;
  private final TextureAtlas.AtlasRegion myLineTex;

  private final Color myAreaWarnCol;
  private final Color myAreaWarnBgCol;

  private boolean myToggled;
  private float myZoom;
  private float mySkullTime;
  private float myAreaSkullTime;
  private final float myIconRad;


  public MapDrawer(TextureManager textureManager, float screenHeight) {
    myZoom = MAX_ZOOM / MUL_FACTOR / MUL_FACTOR;
    float minIconRad = MIN_ICON_RAD_PX / screenHeight;
    myIconRad = ICON_RAD < minIconRad ? minIconRad : ICON_RAD;

    myAreaWarnCol = new Color(SolColor.W);
    myAreaWarnBgCol = new Color(SolColor.UI_WARN);

    myWarnAreaBg = textureManager.getTex(MAP_TEX_DIR + "warnBg", null);
    myAtmTex = textureManager.getTex(MAP_TEX_DIR + "atm", null);
    myPlanetTex = textureManager.getTex(MAP_TEX_DIR + "planet", null);
    myPlanetCoreTex = textureManager.getTex(MAP_TEX_DIR + "planetCore", null);
    myStarTex = textureManager.getTex(MAP_TEX_DIR + "star", null);
    myMazeTex = textureManager.getTex(MAP_TEX_DIR + "maze", null);
    mySkullBigTex = textureManager.getTex(MAP_TEX_DIR + "skullBig", null);
    myBeltTex = textureManager.getTex(MAP_TEX_DIR + "asteroids", null);
    myBeaconAttackTex = textureManager.getTex(MAP_TEX_DIR + "beaconAttack", null);
    myBeaconMoveTex = textureManager.getTex(MAP_TEX_DIR + "beaconMove", null);
    myBeaconFollowTex = textureManager.getTex(MAP_TEX_DIR + "beaconFollow", null);
    myWhiteTex = textureManager.getTex(MAP_TEX_DIR + "whiteTex", null);
    myLineTex = textureManager.getTex(MAP_TEX_DIR + "gridLine", null);

    myIconBg = textureManager.getTex(TextureManager.HULL_ICONS_DIR + "bg", null);
    mySkullTex = textureManager.getTex(TextureManager.HULL_ICONS_DIR + "skull", null);
    myStarPortTex = textureManager.getTex(TextureManager.HULL_ICONS_DIR + "starPort", null);
  }

  public boolean isToggled() {
    return myToggled;
  }

  public void draw(GameDrawer drawer, ManiGame game) {
    ManiCam cam = game.getCam();
    float iconSz = getIconRadius(cam) * 2;
    float starNodeW = cam.getViewHeight(myZoom) * STAR_NODE_SZ;
    float viewDist = cam.getViewDist(myZoom);
    FactionManager factionManager = game.getFactionMan();
    ManiShip hero = game.getHero();
    Planet np = game.getPlanetMan().getNearestPlanet();
    Vector2 camPos = cam.getPos();
    float camAngle = cam.getAngle();
    float heroDmgCap = hero == null ? Float.MAX_VALUE : HardnessCalc.getShipDmgCap(hero);

    drawer.updateMtx(game);
    game.getGridDrawer().draw(drawer, game, GRID_SZ, myLineTex);
    drawPlanets(drawer, game, viewDist, np, camPos, heroDmgCap, camAngle);
    drawMazes(drawer, game, viewDist, np, camPos, heroDmgCap, camAngle);
    drawStarNodes(drawer, game, viewDist, camPos, starNodeW);

    // using ui textures
    drawIcons(drawer, game, iconSz, viewDist, factionManager, hero, camPos, heroDmgCap);
  }

  public float getIconRadius(ManiCam cam) {
    return cam.getViewHeight(myZoom) * myIconRad;
  }

  private void drawMazes(GameDrawer drawer, ManiGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap,
                         float camAngle)
  {
    ArrayList<Maze> mazes = game.getPlanetMan().getMazes();
    for (int i = 0, mazesSize = mazes.size(); i < mazesSize; i++) {
      Maze maze = mazes.get(i);
      Vector2 mazePos = maze.getPos();
      float outerRad = maze.getRadius();
      float rad = outerRad - MazeBuilder.BORDER;
      if (viewDist < camPos.dst(mazePos) - rad) continue;
      drawer.draw(myMazeTex, 2 * rad, 2 * rad, rad, rad, mazePos.x, mazePos.y, 45, SolColor.W);
      if (HardnessCalc.isDangerous(heroDmgCap, maze.getDps())) {
        drawAreaDanger(drawer, outerRad, mazePos, 1, camAngle);
      }
    }

  }

  private void drawPlanets(GameDrawer drawer, ManiGame game, float viewDist, Planet np, Vector2 camPos, float heroDmgCap,
                           float camAngle)
  {
    ArrayList<ManiSystem> systems = game.getPlanetMan().getSystems();
    ManiCam cam = game.getCam();
    float circleWidth = cam.getRealLineWidth() * 6;
    float vh = cam.getViewHeight(myZoom);
    for (int i3 = 0, systemsSize1 = systems.size(); i3 < systemsSize1; i3++) {
      ManiSystem sys = systems.get(i3);
      drawer.drawCircle(myLineTex, sys.getPos(), sys.getRadius(), SolColor.UI_MED, circleWidth, vh);
    }
    for (int i2 = 0, systemsSize = systems.size(); i2 < systemsSize; i2++) {
      ManiSystem sys = systems.get(i2);
      float dangerRad = HardnessCalc.isDangerous(heroDmgCap, sys.getDps()) ? sys.getRadius() : 0;
      Vector2 sysPos = sys.getPos();
      float rad = Const.SUN_RADIUS;
      if (camPos.dst(sysPos) - rad < viewDist) {
        drawer.draw(myStarTex, 2 * rad, 2 * rad, rad, rad, sysPos.x, sysPos.y, 0, SolColor.W);
      }

      Vector2 beltIconPos = SolMath.getVec();
      ArrayList<SystemBelt> belts = sys.getBelts();
      for (int i1 = 0, beltsSize = belts.size(); i1 < beltsSize; i1++) {
        SystemBelt belt = belts.get(i1);
        float beltRad = belt.getRadius();
        float halfWidth = belt.getHalfWidth();
        int beltIconCount = (int) (.12f * beltRad);
        for (int i = 0; i < beltIconCount; i++) {
          float angle = 360f * i / beltIconCount;
          SolMath.fromAl(beltIconPos, angle, beltRad);
          beltIconPos.add(sysPos);
          drawer.draw(myBeltTex, 2 * halfWidth, 2 * halfWidth, halfWidth, halfWidth, beltIconPos.x, beltIconPos.y, angle * 3, SolColor.W);
        }
        float outerRad = beltRad + halfWidth;
        if (dangerRad < outerRad && HardnessCalc.isDangerous(heroDmgCap, belt.getDps())) dangerRad = outerRad;
      }
      SolMath.free(beltIconPos);
      if (dangerRad < sys.getInnerRad() && HardnessCalc.isDangerous(heroDmgCap, sys.getInnerDps())) {
        dangerRad = sys.getInnerRad();
      }
      if (dangerRad > 0) {
        drawAreaDanger(drawer, dangerRad, sysPos, .5f, camAngle);
      }
    }

    ArrayList<Planet> planets = game.getPlanetMan().getPlanets();
    for (int i = 0, planetsSize = planets.size(); i < planetsSize; i++) {
      Planet planet = planets.get(i);
      Vector2 planetPos = planet.getPos();
      float fh = planet.getFullHeight();
      float dstToPlanetAtm = camPos.dst(planetPos) - fh;
      if (viewDist < dstToPlanetAtm) continue;
      drawer.draw(myAtmTex, 2 * fh, 2 * fh, fh, fh, planetPos.x, planetPos.y, 0, SolColor.UI_DARK);
      float gh;
      if (dstToPlanetAtm < 0) {
        gh = planet.getMinGroundHeight() + .5f;
        drawer.draw(myPlanetCoreTex, 2 * gh, 2 * gh, gh, gh, planetPos.x, planetPos.y, planet.getAngle(), SolColor.W);
        drawNpGround(drawer, game, viewDist, np, camPos);
      } else {
        gh = planet.getGroundHeight();
        drawer.draw(myPlanetTex, 2 * gh, 2 * gh, gh, gh, planetPos.x, planetPos.y, camAngle, SolColor.W);
      }
      float dangerRad = HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps()) ? gh + Const.ATM_HEIGHT/2 : 0;
//      if (dangerRad < gh && HardnessCalc.isDangerous(heroDmgCap, planet.getGroundDps())) dangerRad = gh;
      if (dangerRad > 0) {
        drawAreaDanger(drawer, dangerRad, planetPos, 1, camAngle);
      }
    }
  }

  private void drawAreaDanger(GameDrawer drawer, float rad, Vector2 pos, float transpMul, float angle) {
    float perc = 2 * myAreaSkullTime / MAX_AREA_SKULL_TIME;
    if (perc > 1) perc = 2 - perc;
    perc = SolMath.clamp((perc - .5f) * 2 + .5f);
    float a = SolMath.clamp(perc * transpMul);
    myAreaWarnBgCol.a = a;
    myAreaWarnCol.a = a;
    drawer.draw(myWarnAreaBg, rad *2, rad *2, rad, rad, pos.x, pos.y, 0, myAreaWarnBgCol);
    rad *= INNER_AREA_ICON_PERC;
    drawer.draw(mySkullBigTex, rad *2, rad *2, rad, rad, pos.x, pos.y, angle, myAreaWarnCol);
  }

  private void drawIcons(GameDrawer drawer, ManiGame game, float iconSz, float viewDist, FactionManager factionManager,
                         ManiShip hero, Vector2 camPos, float heroDmgCap)
  {
    List<ManiObject> objs = game.getObjMan().getObjs();
    for (int i1 = 0, objsSize = objs.size(); i1 < objsSize; i1++) {
      ManiObject o = objs.get(i1);
      Vector2 oPos = o.getPosition();
      if (viewDist < camPos.dst(oPos)) continue;
      if ((o instanceof ManiShip)) {
        ManiShip ship = (ManiShip) o;
        String hint = ship.getPilot().getMapHint();
        if (hint == null && !DebugOptions.DETAILED_MAP) continue;
        drawObjIcon(iconSz, oPos, ship.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, o, ship.getHull().config.getIcon(), drawer);
      }
      if ((o instanceof StarPort)) {
        StarPort sp = (StarPort) o;
        drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
      }
      // Fix for when the player is in hyper. Hero is null and replaced in ObjMan with a StarPort.Transcendent
      if ((o instanceof StarPort.Transcendent)) {
        StarPort.Transcendent t = (StarPort.Transcendent)o;
        if (t.getShip().getPilot().isPlayer()) {
          FarShip ship = game.getTranscendentHero().getShip();
          drawObjIcon(iconSz, oPos, t.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, o, ship.getHullConfig().getIcon(), drawer);
        }

      }
    }

    List<FarShip> farShips = game.getObjMan().getFarShips();
    for (int i = 0, sz = farShips.size(); i < sz; i++) {
      FarShip ship = farShips.get(i);
      Vector2 oPos = ship.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      String hint = ship.getPilot().getMapHint();
      if (hint == null && !DebugOptions.DETAILED_MAP) continue;
      drawObjIcon(iconSz, oPos, ship.getAngle(), factionManager, hero, ship.getPilot().getFaction(), heroDmgCap, ship, ship.getHullConfig().getIcon(), drawer);
    }

    List<StarPort.MyFar> farPorts = game.getObjMan().getFarPorts();
    for (int i = 0, sz = farPorts.size(); i < sz; i++) {
      StarPort.MyFar sp = farPorts.get(i);
      drawStarPortIcon(drawer, iconSz, sp.getFrom(), sp.getTo());
    }
    BeaconHandler bh = game.getBeaconHandler();
    BeaconHandler.Action bhAction = bh.getCurrAction();
    if (bhAction != null) {
      Vector2 beaconPos = bh.getPos();
      TextureRegion icon = myBeaconMoveTex;
      if (bhAction == BeaconHandler.Action.ATTACK) icon = myBeaconAttackTex;
      else if (bhAction == BeaconHandler.Action.FOLLOW) icon = myBeaconFollowTex;
      float beaconSz = iconSz * 1.5f;
//      drawer.draw(icon, beaconSz, beaconSz, beaconSz/2, beaconSz/2, beaconPos.x, beaconPos.y, 0, SolColor.W); interleaving
    }
  }

  public void drawStarPortIcon(GameDrawer drawer, float iconSz, Planet from, Planet to) {
    float angle = SolMath.angle(from.getPos(), to.getPos());
    Vector2 pos = StarPort.getDesiredPos(from, to, false);
    drawObjIcon(iconSz, pos, angle, null, null, null, -1, null, myStarPortTex, drawer);
    SolMath.free(pos);
  }

  private void drawStarNodes(GameDrawer drawer, ManiGame game, float viewDist, Vector2 camPos, float starNodeW)
  {
    List<ManiObject> objs = game.getObjMan().getObjs();
    for (int i1 = 0, objsSize = objs.size(); i1 < objsSize; i1++) {
      ManiObject o = objs.get(i1);
      if (!(o instanceof StarPort)) continue;
      Vector2 oPos = o.getPosition();
      if (viewDist < camPos.dst(oPos)) continue;
      StarPort sp = (StarPort) o;
      drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
    }

    List<StarPort.MyFar> farPorts = game.getObjMan().getFarPorts();
    for (int i = 0, sz = farPorts.size(); i < sz; i++) {
      StarPort.MyFar sp = farPorts.get(i);
      Vector2 oPos = sp.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      if (!sp.isSecondary()) drawStarNode(drawer, sp.getFrom(), sp.getTo(), starNodeW);
    }
  }

  private void drawStarNode(GameDrawer drawer, Planet from, Planet to, float starNodeW) {
    Vector2 pos1 = StarPort.getDesiredPos(from, to, false);
    Vector2 pos2 = StarPort.getDesiredPos(to, from, false);
    drawer.drawLine(myWhiteTex, pos1, pos2, SolColor.UI_LIGHT, starNodeW, true);
    SolMath.free(pos1);
    SolMath.free(pos2);
  }

  private void drawNpGround(GameDrawer drawer, ManiGame game, float viewDist, Planet np, Vector2 camPos) {
    ObjectManager objectManager = game.getObjMan();
    List<ManiObject> objs = objectManager.getObjs();
    for (int i1 = 0, objsSize = objs.size(); i1 < objsSize; i1++) {
      ManiObject o = objs.get(i1);
      if (!(o instanceof TileObject)) continue;
      TileObject to = (TileObject) o;
      if (to.getPlanet() != np) continue;
      Vector2 oPos = o.getPosition();
      if (viewDist < camPos.dst(oPos)) continue;
      float sz = to.getSz();
      drawPlanetTile(to.getTile(), sz, drawer, oPos, to.getAngle());
    }

    List<FarObjData> farObjs = objectManager.getFarObjs();
    for (int i = 0, farObjsSize = farObjs.size(); i < farObjsSize; i++) {
      FarObjData fod = farObjs.get(i);
      FarObj o = fod.fo;
      if (!(o instanceof FarTileObject)) continue;
      FarTileObject to = (FarTileObject) o;
      if (to.getPlanet() != np) continue;
      Vector2 oPos = o.getPos();
      if (viewDist < camPos.dst(oPos)) continue;
      float sz = to.getSz();
      drawPlanetTile(to.getTile(), sz, drawer, oPos, to.getAngle());
    }
  }

  public void drawObjIcon(float iconSz, Vector2 pos, float objAngle,
                          FactionManager factionManager, ManiShip hero, Faction objFac, float heroDmgCap,
                          Object shipHack, TextureAtlas.AtlasRegion icon, Object drawerHack)
  {
    boolean enemy = hero != null && factionManager.areEnemies(objFac, hero.getPilot().getFaction());
    float angle = objAngle;
    if (enemy && mySkullTime > 0 && HardnessCalc.isDangerous(heroDmgCap, shipHack)) {
      icon = mySkullTex;
      angle = 0;
    }
    float innerIconSz = iconSz * INNER_ICON_PERC;

    if (drawerHack instanceof UiDrawer) {
      UiDrawer uiDrawer = (UiDrawer) drawerHack;
      uiDrawer.draw(myIconBg, iconSz, iconSz, iconSz/2, iconSz/2, pos.x, pos.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
      uiDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz/2, innerIconSz/2, pos.x, pos.y, angle, SolColor.W);
    } else {
      GameDrawer gameDrawer = (GameDrawer) drawerHack;
      gameDrawer.draw(myIconBg, iconSz, iconSz, iconSz/2, iconSz/2, pos.x, pos.y, 0, enemy ? SolColor.UI_WARN : SolColor.UI_LIGHT);
      gameDrawer.draw(icon, innerIconSz, innerIconSz, innerIconSz/2, innerIconSz/2, pos.x, pos.y, angle, SolColor.W);
    }
  }

  public void setToggled(boolean toggled) {
    myToggled = toggled;
  }

  public void changeZoom(boolean zoomIn) {
    if (zoomIn) myZoom /= MUL_FACTOR; else myZoom *= MUL_FACTOR;
    myZoom = SolMath.clamp(myZoom, MIN_ZOOM, MAX_ZOOM);
  }

  public float getZoom() {
    return myZoom;
  }

  public void update(ManiGame game) {
    mySkullTime += game.getTimeStep();
    if (mySkullTime > MAX_SKULL_TIME) mySkullTime = -MAX_SKULL_TIME;
    myAreaSkullTime += game.getTimeStep();
    if (myAreaSkullTime > MAX_AREA_SKULL_TIME) myAreaSkullTime = 0;
  }

  private void drawPlanetTile(Tile t, float sz, GameDrawer drawer, Vector2 p, float angle) {
    float szh = .6f * sz;
    Color col = t.from == SurfaceDirection.UP && t.to == SurfaceDirection.UP ? SolColor.W : SolColor.UI_OPAQUE;
    if (t.from == SurfaceDirection.FWD || t.from == SurfaceDirection.UP) {
      if (t.from == SurfaceDirection.UP) drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle - 90, col);
      drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle, col);
    }
    if (t.to == SurfaceDirection.FWD || t.to == SurfaceDirection.UP) {
      if (t.to == SurfaceDirection.UP) drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle + 180, col);
      drawer.draw(myWhiteTex, szh, szh, 0, 0, p.x, p.y, angle + 90, col);
    }
  }

  public TextureAtlas.AtlasRegion getStarPortTex() {
    return myStarPortTex;
  }

}