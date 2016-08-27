

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.ManiGame;

public interface ManiItem {
  String getDisplayName();
  float getPrice();
  String getDesc();
  ManiItem copy();
  boolean isSame(ManiItem item);
  TextureAtlas.AtlasRegion getIcon(ManiGame game);
  ManiItemType getItemType();
  String getCode();
  int isEquipped();
  void setEquipped(int equipped);
}
