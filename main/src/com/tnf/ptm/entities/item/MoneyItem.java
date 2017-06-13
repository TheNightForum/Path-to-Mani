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
import com.tnf.ptm.common.PtmGame;

public class MoneyItem implements PtmItem {
    public static final int AMT = 10;
    public static final int MED_AMT = 3 * AMT;
    public static final int BIG_AMT = 10 * AMT;

    private final float myAmt;
    private final PtmItemType myItemType;

    public MoneyItem(float amt, PtmItemType itemType) {
        myAmt = amt;
        myItemType = itemType;
    }

    @Override
    public String getDisplayName() {
        return "money";
    }

    @Override
    public float getPrice() {
        return myAmt;
    }

    @Override
    public String getDesc() {
        return "money";
    }

    @Override
    public MoneyItem copy() {
        return new MoneyItem(myAmt, myItemType);
    }

    @Override
    public boolean isSame(PtmItem item) {
        return item instanceof MoneyItem && ((MoneyItem) item).myAmt == myAmt;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(PtmGame game) {
        ItemManager im = game.getItemMan();
        if (myAmt == BIG_AMT) {
            return im.bigMoneyIcon;
        }
        if (myAmt == MED_AMT) {
            return im.medMoneyIcon;
        }
        return im.moneyIcon;
    }

    @Override
    public PtmItemType getItemType() {
        return myItemType;
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
}
