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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.pathtomani.*;
import com.pathtomani.common.*;
import com.pathtomani.game.dra.DraDebugger;
import com.pathtomani.entities.gun.GunItem;
import com.pathtomani.entities.item.ItemContainer;
import com.pathtomani.entities.item.ItemManager;
import com.pathtomani.entities.item.LootBuilder;
import com.pathtomani.entities.item.ManiItem;
import com.pathtomani.entities.planet.Planet;
import com.pathtomani.entities.ship.*;
import com.pathtomani.game.sound.SoundManager;
import com.pathtomani.files.FileManager;
import com.pathtomani.files.HullConfigManager;
import com.pathtomani.entities.asteroid.AsteroidBuilder;
import com.pathtomani.game.chunk.ChunkManager;
import com.pathtomani.game.dra.DraMan;
import com.pathtomani.game.farBg.FarBackgroundManagerOld;
import com.pathtomani.game.input.AiPilot;
import com.pathtomani.game.input.BeaconDestProvider;
import com.pathtomani.game.input.Pilot;
import com.pathtomani.game.input.UiControlledPilot;
import com.pathtomani.gfx.GameColors;
import com.pathtomani.gfx.particle.EffectTypes;
import com.pathtomani.gfx.particle.PartMan;
import com.pathtomani.gfx.particle.SpecialEffects;
import com.pathtomani.entities.planet.PlanetManager;
import com.pathtomani.entities.planet.ManiSystem;
import com.pathtomani.entities.planet.SunSingleton;
import com.pathtomani.game.screens.GameScreens;
import com.pathtomani.entities.ship.hulls.HullConfig;
import com.pathtomani.game.sound.SpecialSounds;
import com.pathtomani.gfx.CommonDrawer;
import com.pathtomani.gfx.TextureManager;
import com.pathtomani.ui.DebugCollector;
import com.pathtomani.ui.TutorialManager;
import com.pathtomani.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class ManiGame {

  private final GameScreens myScreens;
  private final ManiCam myCam;
  private final ObjectManager myObjectManager;
  private final ManiApplication myCmp;
  private final DraMan myDraMan;
  private final PlanetManager myPlanetManager;
  private final TextureManager myTextureManager;
  private final ChunkManager myChunkManager;
  private final PartMan myPartMan;
  private final AsteroidBuilder myAsteroidBuilder;
  private final LootBuilder myLootBuilder;
  private final ShipBuilder myShipBuilder;
  private final HullConfigManager hullConfigManager;
  private final GridDrawer myGridDrawer;
  private final FarBackgroundManagerOld myFarBackgroundManagerOld;
  private final FactionManager myFactionManager;
  private final MapDrawer myMapDrawer;
  private final ShardBuilder myShardBuilder;
  private final ItemManager myItemManager;
  private final StarPort.Builder myStarPortBuilder;
  private final SoundManager mySoundManager;
  private final PlayerSpawnConfig myPlayerSpawnConfig;
  private final DraDebugger myDraDebugger;
  private final SpecialSounds mySpecialSounds;
  private final EffectTypes myEffectTypes;
  private final SpecialEffects mySpecialEffects;
  private final GameColors gameColors;
  private final AbilityCommonConfigs myAbilityCommonConfigs;
  private final ManiNames myNames;
  private final BeaconHandler myBeaconHandler;
  private final MountDetectDrawer myMountDetectDrawer;
  private final TutorialManager myTutorialManager;

  private ManiShip myHero;
  private float myTimeStep;
  private float myTime;
  private boolean myPaused;
  private final GalaxyFiller myGalaxyFiller;
  private StarPort.Transcendent myTranscendentHero;
  private float myTimeFactor;
  private float myRespawnMoney;
  private HullConfig myRespawnHull;
  private final ArrayList<ManiItem> myRespawnItems;

  public ManiGame(ManiApplication cmp, boolean usePrevShip, TextureManager textureManager, boolean tut, CommonDrawer commonDrawer) {
    myCmp = cmp;
    GameDrawer drawer = new GameDrawer(textureManager, commonDrawer);
    gameColors = new GameColors();
    mySoundManager = new SoundManager();
    mySpecialSounds = new SpecialSounds(mySoundManager);
    myDraMan = new DraMan(drawer);
    myCam = new ManiCam(drawer.r);
    myScreens = new GameScreens(drawer.r, cmp);
    myTutorialManager = tut ? new TutorialManager(commonDrawer.r, myScreens, cmp.isMobile(), cmp.getOptions()) : null;
    myTextureManager = textureManager;
    myFarBackgroundManagerOld = new FarBackgroundManagerOld(myTextureManager);
    myShipBuilder = new ShipBuilder();
    myEffectTypes = new EffectTypes();
    mySpecialEffects = new SpecialEffects(myEffectTypes, myTextureManager, gameColors);
    myItemManager = new ItemManager(myTextureManager, mySoundManager, myEffectTypes, gameColors);
    myAbilityCommonConfigs = new AbilityCommonConfigs(myEffectTypes, myTextureManager, gameColors, mySoundManager);
    hullConfigManager = new HullConfigManager(myShipBuilder, FileManager.getInstance(), textureManager, myItemManager, myAbilityCommonConfigs, mySoundManager);
    myNames = new ManiNames();
    myPlanetManager = new PlanetManager(myTextureManager, hullConfigManager, gameColors, myItemManager);
    ManiContactListener contactListener = new ManiContactListener(this);
    myFactionManager = new FactionManager();
    myObjectManager = new ObjectManager(contactListener, myFactionManager);
    myGridDrawer = new GridDrawer(textureManager);
    myChunkManager = new ChunkManager(myTextureManager);
    myPartMan = new PartMan();
    myAsteroidBuilder = new AsteroidBuilder(myTextureManager);
    myLootBuilder = new LootBuilder();
    myMapDrawer = new MapDrawer(myTextureManager, commonDrawer.h);
    myShardBuilder = new ShardBuilder(myTextureManager);
    myGalaxyFiller = new GalaxyFiller();
    myStarPortBuilder = new StarPort.Builder();
    myPlayerSpawnConfig = PlayerSpawnConfig.load(hullConfigManager, myItemManager);
    myDraDebugger = new DraDebugger();
    myBeaconHandler = new BeaconHandler(textureManager);
    myMountDetectDrawer = new MountDetectDrawer(textureManager);
    myRespawnItems = new ArrayList<ManiItem>();
    myTimeFactor = 1;

    // from this point we're ready!
    myPlanetManager.fill(myNames);
    myGalaxyFiller.fill(this);
    ShipConfig startingShip = usePrevShip ? SaveManager.readShip(hullConfigManager, myItemManager) : null;
    createPlayer(startingShip);
    ManiMath.checkVectorsTaken(null);
  }

  // uh, this needs refactoring
  private void createPlayer(ShipConfig prevShip) {
    Vector2 pos = myGalaxyFiller.getPlayerSpawnPos(this);
    myCam.setPos(pos);

    Pilot pilot;
    if (myCmp.getOptions().controlType == GameOptions.CONTROL_MOUSE) {
      myBeaconHandler.init(this, pos);
      pilot = new AiPilot(new BeaconDestProvider(), true, Faction.LAANI, false, "you", Const.AI_DET_DIST);
    } else {
      pilot = new UiControlledPilot(myScreens.mainScreen);
    }

    ShipConfig shipConfig;
    if (DebugOptions.GOD_MODE) {
      shipConfig = myPlayerSpawnConfig.godShipConfig;
    } else if (prevShip != null) {
      shipConfig = prevShip;
    } else {
      shipConfig = myPlayerSpawnConfig.shipConfig;
    }

    float money = myRespawnMoney != 0 ? myRespawnMoney : myTutorialManager != null ? 200 : shipConfig.money;

    HullConfig hull = myRespawnHull != null ? myRespawnHull : shipConfig.hull;

    String itemsStr = !myRespawnItems.isEmpty() ? "" : shipConfig.items;

    boolean giveAmmo = prevShip == null && myRespawnItems.isEmpty();
    myHero = myShipBuilder.buildNewFar(this, new Vector2(pos), null, 0, 0, pilot, itemsStr, hull, null, true, money, null, giveAmmo).toObj(this);

    ItemContainer ic = myHero.getItemContainer();
    if (!myRespawnItems.isEmpty()) {
      for (int i1 = 0, sz = myRespawnItems.size(); i1 < sz; i1++) {
        ManiItem item = myRespawnItems.get(i1);
        ic.add(item);
        // Ensure that previously equipped items stay equipped
        if (item.isEquipped() > 0) {
          if (item instanceof GunItem) {
            myHero.maybeEquip(this, item, item.isEquipped() == 2, true);
          }
          else {
            myHero.maybeEquip(this, item, true);
          }
        }
      }
    } else if (DebugOptions.GOD_MODE) {
      myItemManager.addAllGuns(ic);
    } else if (myTutorialManager != null) {
      for (int i = 0; i < 50; i++) {
        if (ic.groupCount() > 1.5f * Const.ITEM_GROUPS_PER_PAGE) break;
        ManiItem it = myItemManager.random();
        if (!(it instanceof GunItem) && it.getIcon(this) != null && ic.canAdd(it)) {
          ic.add(it.copy());
        }
      }
    }
    ic.seenAll();
    //AiPilot.reEquip(this, myHero);  // Don't change equipped items across load/respawn

    myObjectManager.addObjDelayed(myHero);
    myObjectManager.resetDelays();
  }

  public void onGameEnd() {
    saveShip();
    myObjectManager.dispose();
    mySoundManager.dispose();
  }

  public void saveShip() {
    if (myTutorialManager != null) return;
    HullConfig hull;
    float money;
    ArrayList<ManiItem> items;
    if (myHero != null) {
      hull = myHero.getHull().config;
      money = myHero.getMoney();
      items = new ArrayList<ManiItem>();
      for (List<ManiItem> group : myHero.getItemContainer()) {
        for (ManiItem i : group) {
          items.add(0, i);
        }
      }
    } else if (myTranscendentHero != null) {
      FarShip farH = myTranscendentHero.getShip();
      hull = farH.getHullConfig();
      money = farH.getMoney();
      items = new ArrayList<ManiItem>();
      for (List<ManiItem> group : farH.getIc()) {
        for (ManiItem i : group) {
          items.add(0, i);
        }
      }
    } else {
      hull = myRespawnHull;
      money = myRespawnMoney;
      items = myRespawnItems;
    }
    SaveManager.writeShip(hull, money, items, this);
  }

  public GameScreens getScreens() {
    return myScreens;
  }

  public void update() {
    myDraDebugger.update(this);

    if (myPaused) return;

    myTimeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
    if (myHero != null) {
      ShipAbility ability = myHero.getAbility();
      if (ability instanceof SloMo) {
        float factor = ((SloMo) ability).getFactor();
        myTimeFactor *= factor;
      }
    }
    myTimeStep = Const.REAL_TIME_STEP * myTimeFactor;
    myTime += myTimeStep;

    myPlanetManager.update(this);
    myCam.update(this);
    myChunkManager.update(this);
    myMountDetectDrawer.update(this);
    myObjectManager.update(this);
    myDraMan.update(this);
    myMapDrawer.update(this);
    mySoundManager.update(this);
    myBeaconHandler.update(this);

    myHero = null;
    myTranscendentHero = null;
    List<ManiObject> objs = myObjectManager.getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      ManiObject obj = objs.get(i);
      if ((obj instanceof ManiShip)) {
        ManiShip ship = (ManiShip) obj;
        Pilot prov = ship.getPilot();
        if (prov.isPlayer()) {
          myHero = ship;
          break;
        }
      }
      if (obj instanceof StarPort.Transcendent) {
        StarPort.Transcendent trans = (StarPort.Transcendent) obj;
        FarShip ship = trans.getShip();
        if (ship.getPilot().isPlayer()) {
          myTranscendentHero = trans;
          break;
        }
      }
    }

    if (myTutorialManager != null) myTutorialManager.update();
  }

  public void draw() {
    myDraMan.draw(this);
  }

  public void drawDebug(GameDrawer drawer) {
    if (DebugOptions.GRID_SZ > 0) myGridDrawer.draw(drawer, this, DebugOptions.GRID_SZ, drawer.debugWhiteTex);
    myPlanetManager.drawDebug(drawer, this);
    myObjectManager.drawDebug(drawer, this);
    if (DebugOptions.ZOOM_OVERRIDE != 0) myCam.drawDebug(drawer);
    drawDebugPoint(drawer, DebugOptions.DEBUG_POINT, DebugCol.POINT);
    drawDebugPoint(drawer, DebugOptions.DEBUG_POINT2, DebugCol.POINT2);
    drawDebugPoint(drawer, DebugOptions.DEBUG_POINT3, DebugCol.POINT3);
  }

  private void drawDebugPoint(GameDrawer drawer, Vector2 dp, Color col) {
    if (dp.x != 0 || dp.y != 0) {
      float sz = myCam.getRealLineWidth() * 5;
      drawer.draw(drawer.debugWhiteTex, sz, sz, sz / 2, sz / 2, dp.x, dp.y, 0, col);
    }
  }

  public float getTimeStep() {
    return myTimeStep;
  }

  public ManiCam getCam() {
    return myCam;
  }

  public ManiApplication getCmp() {
    return myCmp;
  }

  public DraMan getDraMan() {
    return myDraMan;
  }

  public ObjectManager getObjMan() {
    return myObjectManager;
  }

  public TextureManager getTexMan() {
    return myTextureManager;
  }

  public PlanetManager getPlanetMan() {
    return myPlanetManager;
  }

  public PartMan getPartMan() {
    return myPartMan;
  }

  public AsteroidBuilder getAsteroidBuilder() {
    return myAsteroidBuilder;
  }

  public LootBuilder getLootBuilder() {
    return myLootBuilder;
  }

  public ManiShip getHero() {
    return myHero;
  }

  public ShipBuilder getShipBuilder() {
    return myShipBuilder;
  }

  public ItemManager getItemMan() {
    return myItemManager;
  }

  public HullConfigManager getHullConfigs() {
    return hullConfigManager;
  }

  public boolean isPaused() {
    return myPaused;
  }

  public void setPaused(boolean paused) {
    myPaused = paused;
    DebugCollector.warn(myPaused ? "game paused" : "game resumed");
  }

  public void respawn() {
    if (myHero != null) {
      beforeHeroDeath();
      myObjectManager.removeObjDelayed(myHero);
    } else if (myTranscendentHero != null) {
      FarShip farH = myTranscendentHero.getShip();
      setRespawnState(farH.getMoney(), farH.getIc(), farH.getHullConfig());
      myObjectManager.removeObjDelayed(myTranscendentHero);
    }
    createPlayer(null);
  }

  public FactionManager getFactionMan() {
    return myFactionManager;
  }

  public boolean isPlaceEmpty(Vector2 pos, boolean considerPlanets) {
    Planet np = myPlanetManager.getNearestPlanet(pos);
    if (considerPlanets) {
      boolean inPlanet = np.getPos().dst(pos) < np.getFullHeight();
      if (inPlanet) return false;
    }
    ManiSystem ns = myPlanetManager.getNearestSystem(pos);
    if (ns.getPos().dst(pos) < SunSingleton.SUN_HOT_RAD) return false;
    List<ManiObject> objs = myObjectManager.getObjs();
    for (int i = 0, objsSize = objs.size(); i < objsSize; i++) {
      ManiObject o = objs.get(i);
      if (!o.hasBody()) continue;
      if (pos.dst(o.getPosition()) < myObjectManager.getRadius(o)) {
        return false;
      }
    }
    List<FarObjData> farObjs = myObjectManager.getFarObjs();
    for (int i = 0, farObjsSize = farObjs.size(); i < farObjsSize; i++) {
      FarObjData fod = farObjs.get(i);
      FarObj o = fod.fo;
      if (!o.hasBody()) continue;
      if (pos.dst(o.getPos()) < o.getRadius()) {
        return false;
      }
    }
    return true;
  }

  public MapDrawer getMapDrawer() {
    return myMapDrawer;
  }

  public ShardBuilder getShardBuilder() {
    return myShardBuilder;
  }

  public FarBackgroundManagerOld getFarBgManOld() {
    return myFarBackgroundManagerOld;
  }

  public GalaxyFiller getGalaxyFiller() {
    return myGalaxyFiller;
  }

  public StarPort.Builder getStarPortBuilder() {
    return myStarPortBuilder;
  }

  public StarPort.Transcendent getTranscendentHero() {
    return myTranscendentHero;
  }

  public GridDrawer getGridDrawer() {
    return myGridDrawer;
  }

  public SoundManager getSoundMan() {
    return mySoundManager;
  }

  public float getTime() {
    return myTime;
  }

  public void drawDebugUi(UiDrawer uiDrawer) {
    myDraDebugger.draw(uiDrawer, this);
  }

  public PlayerSpawnConfig getPlayerSpawnConfig() {
    return myPlayerSpawnConfig;
  }

  public SpecialSounds getSpecialSounds() {
    return mySpecialSounds;
  }

  public SpecialEffects getSpecialEffects() {
    return mySpecialEffects;
  }

  public GameColors getCols() {
    return gameColors;
  }

  public float getTimeFactor() {
    return myTimeFactor;
  }

  public BeaconHandler getBeaconHandler() {
    return myBeaconHandler;
  }

  public MountDetectDrawer getMountDetectDrawer() {
    return myMountDetectDrawer;
  }

  public TutorialManager getTutMan() {
    return myTutorialManager;
  }

  public void beforeHeroDeath() {
    if (myHero == null) return;

    float money = myHero.getMoney();
    ItemContainer ic = myHero.getItemContainer();

    setRespawnState(money, ic, myHero.getHull().config);

    myHero.setMoney(money - myRespawnMoney);
    for (ManiItem item : myRespawnItems) {
      ic.remove(item);
    }
  }

  private void setRespawnState(float money, ItemContainer ic, HullConfig hullConfig) {
    myRespawnMoney = .75f * money;
    myRespawnHull = hullConfig;
    myRespawnItems.clear();
    for (List<ManiItem> group : ic) {
      for (ManiItem item : group) {
        boolean equipped = myHero == null || myHero.maybeUnequip(this, item, false);
        if (equipped || ManiMath.test(.75f)) {
          myRespawnItems.add(0, item);
        }
      }
    }
  }
}
