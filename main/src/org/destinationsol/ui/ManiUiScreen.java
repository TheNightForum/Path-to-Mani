/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.ui;

import org.destinationsol.ManiApplication;

import java.util.List;

public interface ManiUiScreen {
  List<ManiUiControl> getControls();

  void onAdd(ManiApplication cmp);

  void updateCustom(ManiApplication cmp, ManiInputManager.Ptr[] ptrs, boolean clickedOutside);

  boolean isCursorOnBg(ManiInputManager.Ptr ptr);

  void blurCustom(ManiApplication cmp);


  void drawBg(UiDrawer uiDrawer, ManiApplication cmp);

  void drawImgs(UiDrawer uiDrawer, ManiApplication cmp);

  void drawText(UiDrawer uiDrawer, ManiApplication cmp);

  boolean reactsToClickOutside();
}
