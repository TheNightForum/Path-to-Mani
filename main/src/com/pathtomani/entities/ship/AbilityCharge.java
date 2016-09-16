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

package com.pathtomani.entities.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.pathtomani.game.ManiGame;
import com.pathtomani.entities.item.ManiItem;
import com.pathtomani.entities.item.ManiItemType;
import com.pathtomani.entities.item.ManiItemTypes;
import com.pathtomani.gfx.TextureManager;
import com.pathtomani.managers.files.FileManager;
import com.pathtomani.entities.item.ItemManager;

public class AbilityCharge implements ManiItem {
  private final Config myConfig;

  public AbilityCharge(Config config) {
    myConfig = config;
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
    return new AbilityCharge(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof AbilityCharge && ((AbilityCharge) item).myConfig == myConfig;
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

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }


  public static class Config {
    private final TextureAtlas.AtlasRegion icon;
    private final float price;
    private final String displayName;
    private final String desc;
    public final ManiItemType itemType;
    public final String code;

    public Config(TextureAtlas.AtlasRegion icon, float price, String displayName, String desc, ManiItemType itemType,
      String code) {
      this.icon = icon;
      this.price = price;
      this.displayName = displayName;
      this.desc = desc;
      this.itemType = itemType;
      this.code = code;
    }

    public static void load(ItemManager itemManager, TextureManager textureManager, ManiItemTypes types) {
      JsonReader r = new JsonReader();
      FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("abilityCharges.json");
      JsonValue parsed = r.parse(configFile);
      for (JsonValue ammoNode : parsed) {
        String iconName = ammoNode.getString("iconName");
        TextureAtlas.AtlasRegion icon = textureManager.getTex(TextureManager.ICONS_DIR + iconName, configFile);
        float price = ammoNode.getFloat("price");
        String displayName = ammoNode.getString("displayName");
        String desc = ammoNode.getString("desc");
        String code = ammoNode.name;
        Config c = new Config(icon, price, displayName, desc, types.abilityCharge, code);
        AbilityCharge chargeExample = new AbilityCharge(c);
        itemManager.registerItem(chargeExample);
      }
    }
  }
}
