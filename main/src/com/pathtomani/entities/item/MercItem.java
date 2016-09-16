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
import com.pathtomani.game.ShipConfig;

public class MercItem implements ManiItem {
  private final ShipConfig myConfig;
  private final String myDesc;

  public MercItem(ShipConfig config) {
    myConfig = config;
    myDesc = "Has a shield and repairers\n" + ShipItem.makeDesc(myConfig.hull);
  }

  @Override
  public String getDisplayName() {
    return myConfig.hull.getDisplayName();
  }

  @Override
  public float getPrice() {
    return myConfig.hull.getHirePrice();
  }

  @Override
  public String getDesc() {
    return myDesc;
  }

  @Override
  public ManiItem copy() {
    return new MercItem(myConfig);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof MercItem && ((MercItem) item).myConfig == myConfig;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return myConfig.hull.getIcon();
  }

  @Override
  public ManiItemType getItemType() {
    return ShipItem.EMPTY;
  }

  @Override
  public String getCode() {
    return null;
  }

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }

  public ShipConfig getConfig() {
    return myConfig;
  }
}
