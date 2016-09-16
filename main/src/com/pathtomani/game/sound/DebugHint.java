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

package com.pathtomani.game.sound;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.pathtomani.common.DebugCol;
import com.pathtomani.game.GameDrawer;
import com.pathtomani.game.ManiGame;
import com.pathtomani.game.ManiObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DebugHint {
  private static final long MAX_AWAIT = 3000;
  private final Vector2 myPos;
  private final Map<String, Long> myMsgs;

  private ManiObject myOwner;
  private String myMsg;

  public DebugHint(ManiObject owner, Vector2 pos) {
    myOwner = owner;
    myPos = new Vector2(pos);
    myMsgs = new HashMap<String, Long>();
  }

  public void add(String value) {
    boolean needsRebuild = !myMsgs.containsKey(value);
    myMsgs.put(value, TimeUtils.millis() + MAX_AWAIT);
    if (needsRebuild) rebuild();
  }

  private void rebuild() {
    StringBuilder sb = new StringBuilder();
    for (String msg : myMsgs.keySet()) {
      sb.append(msg).append("\n");
    }
    myMsg = sb.toString();
  }

  public void update(ManiGame game) {
    if (myOwner != null) {
      if (myOwner.shouldBeRemoved(game)) {
        myOwner = null;
      } else {
        myPos.set(myOwner.getPosition());
      }
    }

    long now = TimeUtils.millis();
    boolean needsRebuild = false;
    Iterator<Map.Entry<String, Long>> it = myMsgs.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Long> e = it.next();
      if (e.getValue() <= now) {
        it.remove();
        needsRebuild = true;
      }
    }
    if (needsRebuild) rebuild();
  }

  public boolean shouldRemove() {
    return myMsgs.isEmpty();
  }

  public void draw(GameDrawer drawer, ManiGame game) {
    float fontSz = game.getCam().getDebugFontSize();
    drawer.drawString(myMsg, myPos.x, myPos.y, fontSz, false, DebugCol.HINT);
  }
}
