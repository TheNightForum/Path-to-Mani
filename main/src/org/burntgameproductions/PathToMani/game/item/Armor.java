/*
 * Copyright 2016 MovingBlocks
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

package org.burntgameproductions.PathToMani.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.files.FileManager;
import org.burntgameproductions.PathToMani.game.DmgType;
import org.burntgameproductions.PathToMani.game.sound.ManiSound;
import org.burntgameproductions.PathToMani.game.sound.SoundManager;
import org.burntgameproductions.PathToMani.game.ManiGame;

public class Armor implements ManiItem {
  private final Config myConfig;
  private int myEquipped;

  private Armor(Config config) {
    myConfig = config;
  }

  private Armor(Config config, int equipped) {
    this(config);
    myEquipped = equipped;
  }

  @Override
  public String getDisplayName() {
    return myConfig.displayName;
  }

  @Override
  public float getPrice() {
    return myConfig.price;
  }

  @Override
  public String getDesc() {
    return myConfig.desc;
  }

  @Override
  public ManiItem copy() {
    return new Armor(myConfig, myEquipped);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return false;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return myConfig.icon;
  }

  @Override
  public ManiItemType getItemType() {
    return myConfig.itemType;
  }

  @Override
  public String getCode() {
    return myConfig.code;
  }

  public float getPerc() {
    return myConfig.perc;
  }

  public ManiSound getHitSound(DmgType dmgType) {
    switch (dmgType) {
      case BULLET: return myConfig.bulletHitSound;
      case ENERGY: return myConfig.energyHitSound;
    }
    return null;
  }

  public int isEquipped() { return myEquipped; }

  public void setEquipped(int equipped) { myEquipped = equipped; }

  public static class Config {
    public final String displayName;
    public final int price;
    public final float perc;
    public final String desc;
    public final ManiSound bulletHitSound;
    public final Armor example;
    public final TextureAtlas.AtlasRegion icon;
    public final ManiSound energyHitSound;
    public final ManiItemType itemType;
    public final String code;

    private Config(String displayName, int price, float perc, ManiSound bulletHitSound,
                   TextureAtlas.AtlasRegion icon, ManiSound energyHitSound, ManiItemType itemType, String code)
    {
      this.displayName = displayName;
      this.price = price;
      this.perc = perc;
      this.icon = icon;
      this.energyHitSound = energyHitSound;
      this.itemType = itemType;
      this.code = code;
      this.desc = "Reduces damage by " + (int)(perc * 100) + "%\nStrong against energy guns";
      this.bulletHitSound = bulletHitSound;
      this.example = new Armor(this);
    }

    public static void loadConfigs(ItemManager itemManager, SoundManager soundManager, TextureManager textureManager, ManiItemTypes types)
    {
      JsonReader r = new JsonReader();
      FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("armors.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        float perc = sh.getFloat("perc");
        String bulletDmgSoundDir = sh.getString("bulletHitSound");
        String energyDmgSoundDir = sh.getString("energyHitSound");
        float basePitch = sh.getFloat("baseSoundPitch", 1);
        ManiSound bulletDmgSound = soundManager.getPitchedSound(bulletDmgSoundDir, configFile, basePitch);
        ManiSound energyDmgSound = soundManager.getPitchedSound(energyDmgSoundDir, configFile, basePitch);
        TextureAtlas.AtlasRegion icon = textureManager.getTex(TextureManager.ICONS_DIR + sh.getString("icon"), configFile);
        String code = sh.name;
        Config config = new Config(displayName, price, perc, bulletDmgSound, icon, energyDmgSound, types.armor, code);
        itemManager.registerItem(config.example);
      }
    }
  }
}
