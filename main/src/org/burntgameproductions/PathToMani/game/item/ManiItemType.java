

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.graphics.Color;
import org.burntgameproductions.PathToMani.game.sound.ManiSound;

public class ManiItemType {
  public final Color color;
  public final ManiSound pickUpSound;
  public final Color uiColor;
  public final float sz;

  public ManiItemType(Color color, ManiSound pickUpSound, float sz) {
    this.color = color;
    this.sz = sz;
    uiColor = new Color(color);
    uiColor.a = .3f;
    this.pickUpSound = pickUpSound;
  }

}
