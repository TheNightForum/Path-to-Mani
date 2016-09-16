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

package com.pathtomani.game.dra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.pathtomani.effects.DevTextureProvider;
import com.pathtomani.common.DebugCol;
import com.pathtomani.effects.ManiColor;
import com.pathtomani.game.DebugOptions;
import com.pathtomani.game.ManiGame;
import com.pathtomani.ui.FontSize;
import com.pathtomani.ui.UiDrawer;

import java.util.HashSet;
import java.util.Set;

public class DraDebugger {
  public static final float TEX_SZ = .1f;
  public static final float GAP = .01f;
  private final Set<TextureAtlas.AtlasRegion> myCollector;

  public DraDebugger() {
    myCollector = new HashSet<TextureAtlas.AtlasRegion>();
  }

  public void update(ManiGame game) {
    if (!DebugOptions.TEX_INFO) return;
    maybeCollectTexs(game);
  }

  private void maybeCollectTexs(ManiGame game) {
    if (!Gdx.input.isTouched()) return;
    myCollector.clear();
    Vector2 cursorPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
    game.getCam().screenToWorld(cursorPos);
    game.getDraMan().collectTexs(myCollector, cursorPos);
  }

  public void draw(UiDrawer uiDrawer, ManiGame game) {
    if (!DebugOptions.TEX_INFO) return;
    float y = GAP;
    for (TextureAtlas.AtlasRegion tex : myCollector) {
      float x = GAP;
      uiDrawer.draw(uiDrawer.whiteTex, 5 * TEX_SZ, TEX_SZ + 2 * GAP, 0, 0, x, y, 0, ManiColor.DG);
      y += GAP;
      x += GAP;
      float r = 1f * tex.getTexture().getWidth() / tex.getTexture().getHeight();
      float w = r > 1 ? TEX_SZ : TEX_SZ/r;
      float h = r > 1 ? TEX_SZ/r : TEX_SZ;
      uiDrawer.draw(tex, w, h, w/2, h/2, x + .5f * TEX_SZ, y + .5f * TEX_SZ, 0, ManiColor.W);
      x += TEX_SZ + GAP;
      uiDrawer.drawString(tex.name, x, y, FontSize.DEBUG, false, DebugCol.TEX_INFO);
      y += .5f * TEX_SZ;
      String definedBy = ((DevTextureProvider.SolTex) tex).definedBy;
      uiDrawer.drawString(definedBy, x, y, FontSize.DEBUG, false, DebugCol.TEX_INFO);
      y += .5f * TEX_SZ + 2 * GAP;
    }
  }
}
