

package org.burntgameproductions.PathToMani.game.input;

import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.ship.ManiShip;
import org.burntgameproductions.PathToMani.game.ship.ShipAbility;
import org.burntgameproductions.PathToMani.game.item.ManiItem;

public class AbilityUpdater {
  private final float myAbilityUseStartPerc;
  private final int myChargesToKeep;

  private boolean myAbility;

  public AbilityUpdater() {
    myAbilityUseStartPerc = SolMath.rnd(.3f, .7f);
    myChargesToKeep = SolMath.intRnd(1, 2);
  }

  public void update(ManiShip ship, ManiShip nearestEnemy) {
    myAbility = false;
    if (nearestEnemy == null) return;
    ShipAbility ability = ship.getAbility();
    if (ability == null) return;
    if (ship.getHull().config.getMaxLife() * myAbilityUseStartPerc < ship.getLife()) return;
    ManiItem ex = ability.getConfig().getChargeExample();
    if (ex != null) {
      if (ship.getItemContainer().count(ex) <= myChargesToKeep) return;
    }
    if (ability.getRadius() < nearestEnemy.getPosition().dst(ship.getPosition())) return;
    myAbility = true;
  }

  public boolean isAbility() {
    return myAbility;
  }
}
