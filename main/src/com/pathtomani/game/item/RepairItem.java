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

package com.pathtomani.game.item;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.pathtomani.game.ManiGame;

public class RepairItem implements ManiItem {
  public static final int LIFE_AMT = 20;
  private final ManiItemType myItemType;

  public RepairItem(ManiItemType itemType) {
    myItemType = itemType;
  }

  @Override
  public String getDisplayName() {
    return "Repair Kit";
  }

  @Override
  public float getPrice() {
    return 30;
  }

  @Override
  public String getDesc() {
    return "Stay idle to fix " + LIFE_AMT + " dmg";
  }

  @Override
  public ManiItem copy() {
    return new RepairItem(myItemType);
  }

  @Override
  public boolean isSame(ManiItem item) {
    return item instanceof RepairItem;
  }

  @Override
  public TextureAtlas.AtlasRegion getIcon(ManiGame game) {
    return game.getItemMan().repairIcon;
  }

  @Override
  public ManiItemType getItemType() {
    return myItemType;
  }

  @Override
  public String getCode() {
    return "rep";
  }

  @Override
  public int isEquipped() {
    return 0;
  }

  @Override
  public void setEquipped(int equipped) {

  }
}
