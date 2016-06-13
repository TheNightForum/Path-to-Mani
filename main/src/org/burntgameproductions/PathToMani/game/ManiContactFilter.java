

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.burntgameproductions.PathToMani.game.projectile.Projectile;

public class ManiContactFilter implements ContactFilter {
  private final FactionManager myFactionManager;

  public ManiContactFilter(FactionManager factionManager) {
    myFactionManager = factionManager;
  }

  @Override
  public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
    ManiObject oA = (ManiObject) fixtureA.getBody().getUserData();
    ManiObject oB = (ManiObject) fixtureB.getBody().getUserData();

    boolean aIsProj = oA instanceof Projectile;
    if (!aIsProj && !(oB instanceof Projectile)) return true;

    Projectile proj = (Projectile)(aIsProj ? oA : oB);
    ManiObject o = aIsProj ? oB : oA;
    Fixture f = aIsProj ? fixtureB : fixtureA;
    return proj.shouldCollide(o, f, myFactionManager);
  }
}
