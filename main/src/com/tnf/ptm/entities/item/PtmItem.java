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

public interface PtmItem {
    String getDisplayName();

    float getPrice();

    String getDesc();

    PtmItem copy();

    boolean isSame(PtmItem item);

    TextureAtlas.AtlasRegion getIcon(PtmGame game);

    PtmItemType getItemType();

    String getCode();

    int isEquipped();

    void setEquipped(int equipped);
}
