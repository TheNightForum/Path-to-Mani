

package org.burntgameproductions.PathToMani.ui;

import org.burntgameproductions.PathToMani.ManiApplication;

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
