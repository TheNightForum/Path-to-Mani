

package org.burntgameproductions.PathToMani.game.ship;

import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.game.input.Pilot;
import org.burntgameproductions.PathToMani.game.item.*;
import org.burntgameproductions.PathToMani.game.FarObj;
import org.burntgameproductions.PathToMani.game.RemoveController;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.gun.GunItem;
import org.burntgameproductions.PathToMani.game.ship.hulls.HullConfig;

public class FarShip implements FarObj {
  private final Vector2 myPos;
  private final Vector2 mySpd;
  private final Shield myShield;
  private final Armor myArmor;
  private float myAngle;
  private final float myRotSpd;
  private final Pilot myPilot;
  private final ItemContainer myContainer;
  private final HullConfig myHullConfig;
  private float myLife;
  private final GunItem myGun1;
  private final GunItem myGun2;
  private final RemoveController myRemoveController;
  private final EngineItem myEngine;
  private ShipRepairer myRepairer;
  private float myMoney;
  private final TradeContainer myTradeContainer;

  public FarShip(Vector2 pos, Vector2 spd, float angle, float rotSpd, Pilot pilot, ItemContainer container,
    HullConfig hullConfig, float life,
    GunItem gun1, GunItem gun2, RemoveController removeController, EngineItem engine,
    ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield, Armor armor)
  {
    myPos = pos;
    mySpd = spd;
    myAngle = angle;
    myRotSpd = rotSpd;
    myPilot = pilot;
    myContainer = container;
    myHullConfig = hullConfig;
    myLife = life;
    myGun1 = gun1;
    myGun2 = gun2;
    myRemoveController = removeController;
    myEngine = engine;
    myRepairer = repairer;
    myMoney = money;
    myTradeContainer = tradeContainer;
    myShield = shield;
    myArmor = armor;

    if (myPilot.isPlayer()) {
      if (myShield != null) {
        myShield.setEquipped(1);
      }
      if (myArmor != null) {
        myArmor.setEquipped(1);
      }
      if (myGun1 != null) {
        myGun1.setEquipped(1);
      }
      if (myGun2 != null) {
        myGun2.setEquipped(2);
      }
    }
  }

  @Override
  public boolean shouldBeRemoved(ManiGame game) {
    return myRemoveController != null && myRemoveController.shouldRemove(myPos);
  }

  @Override
  public ManiShip toObj(ManiGame game) {
    return game.getShipBuilder().build(game, myPos, mySpd, myAngle, myRotSpd, myPilot, myContainer, myHullConfig, myLife, myGun1,
      myGun2, myRemoveController, myEngine, myRepairer, myMoney, myTradeContainer, myShield, myArmor);
  }

  @Override
  public void update(ManiGame game) {
    myPilot.updateFar(game, this);
    if (myTradeContainer != null) myTradeContainer.update(game);
    if (myRepairer != null) myLife += myRepairer.tryRepair(game, myContainer, myLife, myHullConfig);
  }

  @Override
  public float getRadius() {
    return myHullConfig.getApproxRadius();
  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public boolean hasBody() {
    return true;
  }

  public void setPos(Vector2 pos) {
    myPos.set(pos);
  }

  public void setSpd(Vector2 spd) {
    mySpd.set(spd);
  }

  public Pilot getPilot() {
    return myPilot;
  }

  public HullConfig getHullConfig() {
    return myHullConfig;
  }

  public float getAngle() {
    return myAngle;
  }

  public Vector2 getSpd() {
    return mySpd;
  }

  public EngineItem getEngine() {
    return myEngine;
  }

  public void setAngle(float angle) {
    myAngle = angle;
  }

  public GunItem getGun(boolean secondary) {
    return secondary ? myGun2 : myGun1;
  }

  public Shield getShield() {
    return myShield;
  }

  public Armor getArmor() {
    return myArmor;
  }

  public float getLife() {
    return myLife;
  }

  public boolean mountCanFix(boolean sec) {
    final int slotNr = (sec) ? 1 : 0;

    return !myHullConfig.getGunSlot(slotNr).allowsRotation();
  }

  public float getMoney() {
    return myMoney;
  }

  public ItemContainer getIc() {
    return myContainer;
  }
}
