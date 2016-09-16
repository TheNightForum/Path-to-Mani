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

package com.pathtomani.entities.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.pathtomani.common.ManiMath;
import com.pathtomani.files.FileManager;
import com.pathtomani.game.DmgType;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.ManiObject;
import com.pathtomani.entities.ship.ManiShip;
import com.pathtomani.game.sound.ManiSound;
import com.pathtomani.game.sound.SoundManager;
import com.pathtomani.effects.TextureManager;

public class Shield implements ManiItem {
  public static final float SIZE_PERC = .7f;
  private static final float BULLET_DMG_FACTOR = .7f;
  private final Config myConfig;
  private float myLife;
  private float myIdleTime;
  private int myEquipped;

  private Shield(Config config) {
    myConfig = config;
    myLife = myConfig.maxLife;
  }

  private Shield(Config config, int equipped) {
    this(config);
    myEquipped = equipped;
  }

  public void update(ManiGame game, ManiObject owner) {
    float ts = game.getTimeStep();
    if (myIdleTime >= myConfig.myMaxIdleTime) {
      if (myLife < myConfig.maxLife) {
        float regen = myConfig.regenSpd * ts;
        myLife = ManiMath.approach(myLife, myConfig.maxLife, regen);
      }
    } else {
      myIdleTime += ts;
      if (myIdleTime >= myConfig.myMaxIdleTime) {
        game.getSoundMan().play(game, myConfig.regenSound, null, owner);
      }
    }
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
    return new Shield(myConfig, myEquipped);
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

  public float getLife() {
    return myLife;
  }

  public float getMaxLife() {
    return myConfig.maxLife;
  }

  public boolean canAbsorb(DmgType dmgType) {
    return myLife > 0 && dmgType != DmgType.FIRE && dmgType != DmgType.CRASH;
  }

  public void absorb(ManiGame game, float dmg, Vector2 pos, ManiShip ship, DmgType dmgType) {
    if (!canAbsorb(dmgType) || dmg <= 0) throw new AssertionError("illegal call to absorb");
    myIdleTime = 0f;
    if (dmgType == DmgType.BULLET) dmg *= BULLET_DMG_FACTOR;
    myLife -= myLife < dmg ? myLife : dmg;

    game.getPartMan().shieldSpark(game, pos, ship.getHull(), myConfig.tex, dmg / myConfig.maxLife);
    float volMul = ManiMath.clamp(4 * dmg / myConfig.maxLife);
    game.getSoundMan().play(game, myConfig.absorbSound, null, ship, volMul);

  }

  public int isEquipped() { return myEquipped; }

  public void setEquipped(int equipped) { myEquipped = equipped; }

  public static class Config {
    public final String displayName;
    public final int price;
    public final String desc;
    public final ManiSound absorbSound;
    public final ManiSound regenSound;
    public final Shield example;
    public final float maxLife;
    public final float myMaxIdleTime = 2;
    public final float regenSpd;
    public final TextureAtlas.AtlasRegion icon;
    public TextureAtlas.AtlasRegion tex;
    public final ManiItemType itemType;
    public final String code;

    private Config(int maxLife, String displayName, int price, ManiSound absorbSound, ManiSound regenSound,
                   TextureAtlas.AtlasRegion icon, TextureAtlas.AtlasRegion tex, ManiItemType itemType, String code) {
      this.maxLife = maxLife;
      this.displayName = displayName;
      this.price = price;
      this.absorbSound = absorbSound;
      this.regenSound = regenSound;
      this.icon = icon;
      this.tex = tex;
      this.itemType = itemType;
      this.code = code;
      regenSpd = this.maxLife / 3;
      example = new Shield(this);
      this.desc = makeDesc();
    }

    private String makeDesc() {
      StringBuilder sb = new StringBuilder();
      sb.append("Takes ").append(ManiMath.nice(maxLife)).append(" dmg\n");
      sb.append("Strong against bullets\n");
      return sb.toString();
    }

    public static void loadConfigs(ItemManager itemManager, SoundManager soundManager, TextureManager textureManager, ManiItemTypes types) {
      JsonReader r = new JsonReader();
      FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("shields.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue sh : parsed) {
        int maxLife = sh.getInt("maxLife");
        String displayName = sh.getString("displayName");
        int price = sh.getInt("price");
        String soundDir = sh.getString("absorbSound");
        float absorbPitch = sh.getFloat("absorbSoundPitch", 1);
        ManiSound absorbSound = soundManager.getPitchedSound(soundDir, configFile, absorbPitch);
        soundDir = sh.getString("regenSound");
        ManiSound regenSound = soundManager.getSound(soundDir, configFile);
        TextureAtlas.AtlasRegion icon = textureManager.getTex(TextureManager.ICONS_DIR + sh.getString("icon"), configFile);
        TextureAtlas.AtlasRegion tex = textureManager.getTex(sh.getString("tex"), configFile);
        String code = sh.name;
        Config config = new Config(maxLife, displayName, price, absorbSound, regenSound, icon, tex, types.shield, code);
        itemManager.registerItem(config.example);
      }
    }
  }
}
