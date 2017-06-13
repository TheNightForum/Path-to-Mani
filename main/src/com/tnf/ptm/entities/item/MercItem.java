/*
 * Copyright 2017 TheNightForum
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

package com.tnf.ptm.entities.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.common.ShipConfig;
import com.tnf.ptm.common.PtmGame;

public class MercItem implements PtmItem {
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
    public PtmItem copy() {
        return new MercItem(myConfig);
    }

    @Override
    public boolean isSame(PtmItem item) {
        return item instanceof MercItem && ((MercItem) item).myConfig == myConfig;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(PtmGame game) {
        return myConfig.hull.getIcon();
    }

    @Override
    public PtmItemType getItemType() {
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
