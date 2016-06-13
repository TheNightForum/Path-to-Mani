

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import org.burntgameproductions.PathToMani.common.SolMath;
import org.burntgameproductions.PathToMani.game.projectile.Projectile;

public class ManiContactListener implements ContactListener {
  private final ManiGame myGame;

  public ManiContactListener(ManiGame game) {
    myGame = game;
  }

  @Override
  public void beginContact(Contact contact) {
    ManiObject oA = (ManiObject) contact.getFixtureA().getBody().getUserData();
    ManiObject oB = (ManiObject) contact.getFixtureB().getBody().getUserData();

    boolean aIsProj = oA instanceof Projectile;
    if (!aIsProj && !(oB instanceof Projectile)) return;

    Projectile proj = (Projectile)(aIsProj ? oA : oB);
    ManiObject o = aIsProj ? oB : oA;
    proj.setObstacle(o, myGame);
  }

  @Override
  public void endContact(Contact contact) {
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
    ManiObject soa = (ManiObject) contact.getFixtureA().getBody().getUserData();
    ManiObject sob = (ManiObject) contact.getFixtureB().getBody().getUserData();
    if (soa instanceof Projectile && ((Projectile) soa).getConfig().density <= 0) return;
    if (sob instanceof Projectile && ((Projectile) sob).getConfig().density <= 0) return;

    float absImpulse = calcAbsImpulse(impulse);
    Vector2 collPos = contact.getWorldManifold().getPoints()[0];
    soa.handleContact(sob, impulse, true, absImpulse, myGame, collPos);
    sob.handleContact(soa, impulse, false, absImpulse, myGame, collPos);
    myGame.getSpecialSounds().playColl(myGame, absImpulse, soa, collPos);
    myGame.getSpecialSounds().playColl(myGame, absImpulse, sob, collPos);
  }

  private float calcAbsImpulse(ContactImpulse impulse) {
    float absImpulse = 0;
    int pointCount = impulse.getCount();
    float[] normImpulses = impulse.getNormalImpulses();
    for (int i = 0; i < pointCount; i++) {
      float normImpulse = normImpulses[i];
      normImpulse = SolMath.abs(normImpulse);
      if (absImpulse < normImpulse) absImpulse = normImpulse;
    }
    return absImpulse;
  }
}
