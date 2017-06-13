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
package com.tnf.ptm.screens.controlers;

import com.tnf.ptm.PtmApplication;

import java.util.List;

public interface PtmUiScreen {
    List<PtmUiControl> getControls();

    default void onAdd(PtmApplication ptmApplication) {
        // Intentionally left blank
    }

    default void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        // Intentionally left blank
    }

    default boolean isCursorOnBg(PtmInputManager.InputPointer inputPointer) {
        return false;
    }

    default void blurCustom(PtmApplication ptmApplication) {
        // Intentionally left blank
    }

    default void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        // Intentionally left blank
    }

    default void drawImgs(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        // Intentionally left blank
    }

    default void drawText(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        // Intentionally left blank
    }

    default boolean reactsToClickOutside() {
        return false;
    }
}
