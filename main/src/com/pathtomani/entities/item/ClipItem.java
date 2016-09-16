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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.pathtomani.game.ManiGame;

public class ClipItem implements ManiItem {
  private final ClipConfig myConfig;

  public ClipItem(ClipConfig config) {
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

  public ClipConfig getConfig() {
    return myConfig;
  }

  @Override
  public ManiItem copy() {
    return new ClipItem(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof ClipItem && ((ClipItem) item).myConfig == myConfig;
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
}
