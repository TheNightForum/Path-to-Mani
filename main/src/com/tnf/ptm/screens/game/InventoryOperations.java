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
package com.tnf.ptm.screens.game;

import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.item.ItemContainer;
import old.tnf.ptm.game.item.PtmItem;
import com.tnf.ptm.screens.controlers.PtmUiScreen;

public interface InventoryOperations extends PtmUiScreen {
    ItemContainer getItems(PtmGame game);

    default boolean isUsing(PtmGame game, PtmItem item) {
        return false;
    }

    default float getPriceMul() {
        return 1;
    }

    String getHeader();
}
