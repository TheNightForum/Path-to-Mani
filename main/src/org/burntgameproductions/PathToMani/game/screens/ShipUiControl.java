

package org.burntgameproductions.PathToMani.game.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.ManiApplication;

public interface ShipUiControl {
  void update(ManiApplication cmp, boolean enabled);
  boolean isLeft();
  boolean isRight();
  boolean isUp();
  boolean isDown();
  boolean isShoot();
  boolean isShoot2();
  boolean isAbility();
  TextureAtlas.AtlasRegion getInGameTex();
  void blur();
}
