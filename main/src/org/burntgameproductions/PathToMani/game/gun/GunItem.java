

package org.burntgameproductions.PathToMani.game.gun;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.burntgameproductions.PathToMani.game.item.ManiItem;
import org.burntgameproductions.PathToMani.game.item.ManiItemType;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class GunItem implements ManiItem {

  public final GunConfig config;
  public int ammo;
  public float reloadAwait;
  private int myEquipped;

  public GunItem(GunConfig config, int ammo, float reloadAwait) {
    this.config = config;
    this.ammo = ammo;
    this.reloadAwait = reloadAwait;
  }

  public GunItem(GunConfig config, int ammo, float reloadAwait, int equipped) {
    this(config, ammo, reloadAwait);
    this.myEquipped = equipped;
  }

  @Override
  public String getDisplayName() {
    return config.displayName;
  }

  @Override
  public float getPrice() {
    return config.price;
  }

  @Override
  public String getDesc() {
    return config.desc;
  }

  @Override
  public GunItem copy() {
    return new GunItem(config, ammo, reloadAwait, myEquipped);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return false;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return config.icon;
  }

  @Override
  public ManiItemType getItemType() {
    return config.itemType;
  }

  @Override
  public String getCode() {
    return config.code;
  }

  public boolean canShoot() {
    return ammo > 0 || reloadAwait > 0;
  }

  public int isEquipped() { return myEquipped; }

  public void setEquipped(int equipped) { myEquipped = equipped; }
}
