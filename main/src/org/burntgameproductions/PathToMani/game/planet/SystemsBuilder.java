

package org.burntgameproductions.PathToMani.game.planet;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.Const;
import org.burntgameproductions.PathToMani.game.DebugOptions;
import org.burntgameproductions.PathToMani.game.ManiNames;
import org.burntgameproductions.PathToMani.game.maze.Maze;
import org.burntgameproductions.PathToMani.game.maze.MazeConfig;
import org.burntgameproductions.PathToMani.game.maze.MazeConfigs;

import java.util.ArrayList;
import java.util.List;

public class SystemsBuilder {
  public static final int SYS_COUNT = 50;//This here tells how many solar systems to build
  //TODO: MAKE ^^^ NEVER ENDING AND LOAD IT IN CHUNCKS
  //TODO: Maybe dumb it down a bit for android version. :)
  //TODO: Add solar system names.
  public static final int MAZE_COUNT = SYS_COUNT * 2;
  public static final int PLANET_COUNT = 5;
  public static final float PLANET_SPD = .2f;
  private static final float GROUND_SPD = .2f;
  private static final float MAX_MAZE_RADIUS = 40f;
  private static final float MAZE_GAP = 10f;
  private static final float BELT_HALF_WIDTH = 20f;

  public List<ManiSystem> build(List<ManiSystem> systems, List<Planet> planets, ArrayList<SystemBelt> belts,
                                PlanetConfigs planetConfigs,
                                MazeConfigs mazeConfigs, ArrayList<Maze> mazes, SysConfigs sysConfigs, ManiNames names)
  {
    int sysLeft = SYS_COUNT;
    int mazesLeft = MAZE_COUNT;
    while (sysLeft > 0 || mazesLeft > 0) {
      boolean createSys = sysLeft > 0;
      if (createSys && mazesLeft > 0 && !systems.isEmpty()) createSys = ManiMath.test(.5f);
      if (createSys) {
        List<Float> ghs = generatePlanetGhs();
        float sysRadius = calcSysRadius(ghs);
        Vector2 pos = getBodyPos(systems, mazes, sysRadius);
        ManiSystem s = createSystem(ghs, pos, planets, belts, planetConfigs, sysRadius, sysConfigs, names, systems.isEmpty());
        systems.add(s);
        sysLeft--;
      } else {
        MazeConfig mc = ManiMath.elemRnd(mazeConfigs.configs);
        float mazeRadius = ManiMath.rnd(.7f, 1) * MAX_MAZE_RADIUS;
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
      boolean createBelt = !beltCreated && 0 < i && i < .5f * PLANET_COUNT && ManiMath.test(.6f);
      float gh;
      if (!createBelt) {
        gh = ManiMath.rnd(.5f, 1) * Const.MAX_GROUND_HEIGHT;
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

  private Vector2 getBodyPos(List<ManiSystem> systems, ArrayList<Maze> mazes, float bodyRadius) {
    Vector2 res = new Vector2();
    float dist = 0;
    while (true) {
      for (int i = 0; i < 20; i++) {
        float angle = ManiMath.rnd(180);
        ManiMath.fromAl(res, angle, dist);
        boolean good = true;
        for (ManiSystem system : systems) {
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
        if (good) return res;
      }
      dist += Const.SUN_RADIUS;
    }
  }

  private ManiSystem createSystem(List<Float> ghs, Vector2 sysPos, List<Planet> planets, ArrayList<SystemBelt> belts,
                                  PlanetConfigs planetConfigs,
                                  float sysRadius, SysConfigs sysConfigs, ManiNames names, boolean firstSys)
  {
    boolean hard = !firstSys;
    String st = DebugOptions.FORCE_SYSTEM_TYPE;
    SysConfig sysConfig;
    if (st.isEmpty()) {
      sysConfig = sysConfigs.getRandomCfg(hard);
    } else {
      sysConfig = sysConfigs.getConfig(st);
    }
    String name = firstSys ? ManiMath.elemRnd(names.systems) : "Sol"; //hack
    ManiSystem s = new ManiSystem(sysPos, sysConfig, name, sysRadius);
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
    if (ManiMath.abs(sysRadius - planetDist) > .1f) throw new AssertionError(sysRadius + " " + planetDist);
    return s;
  }

  private Planet createPlanet(float planetDist, ManiSystem s, float groundHeight, PlanetConfig planetConfig,
                              ManiNames names) {
    float toSysRotSpd = ManiMath.arcToAngle(PLANET_SPD, planetDist) * ManiMath.toInt(ManiMath.test(.5f));
    float rotSpd = ManiMath.arcToAngle(GROUND_SPD, groundHeight)  * ManiMath.toInt(ManiMath.test(.5f));
    String name = ManiMath.elemRnd(names.planets);
    return new Planet(s, ManiMath.rnd(180), planetDist, ManiMath.rnd(180), toSysRotSpd, rotSpd, groundHeight, false, planetConfig, name);
  }

}
