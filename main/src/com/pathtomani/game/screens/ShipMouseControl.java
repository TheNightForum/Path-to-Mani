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

package com.pathtomani.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.pathtomani.ManiApplication;
import com.pathtomani.game.BeaconHandler;
import com.pathtomani.game.ManiGame;
import com.pathtomani.ui.ManiInputManager;
import com.pathtomani.entities.ship.ManiShip;

public class ShipMouseControl implements ShipUiControl {
  private final TextureAtlas.AtlasRegion myMoveCursor;
  private final TextureAtlas.AtlasRegion myAttackCursor;
  private final TextureAtlas.AtlasRegion myFollowCursor;
  private final Vector2 myMouseWorldPos;

  private TextureAtlas.AtlasRegion myCursor;

  public ShipMouseControl(ManiApplication cmp) {
    myMoveCursor = cmp.getTexMan().getTex("ui/cursorMove", null);
    myAttackCursor = cmp.getTexMan().getTex("ui/cursorAttack", null);
    myFollowCursor = cmp.getTexMan().getTex("ui/cursorFollow", null);
    myMouseWorldPos = new Vector2();
  }

  @Override
  public void update(ManiApplication cmp, boolean enabled) {
    ManiGame g = cmp.getGame();
    ManiShip h = g.getHero();
    myCursor = null;
    if (h != null) {
      myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
      g.getCam().screenToWorld(myMouseWorldPos);
      ManiInputManager im = cmp.getInputMan();
      boolean clicked = im.getPtrs()[0].pressed;
      boolean onMap = im.isScreenOn(g.getScreens().mapScreen);
      BeaconHandler.Action a = g.getBeaconHandler().processMouse(g, myMouseWorldPos, clicked, onMap);
      if (a == BeaconHandler.Action.ATTACK) {
        myCursor = myAttackCursor;
      } else if (a == BeaconHandler.Action.FOLLOW) {
        myCursor = myFollowCursor;
      } else {
        myCursor = myMoveCursor;
      }
    }
  }

  @Override
  public boolean isLeft() {
    return false;
  }

  @Override
  public boolean isRight() {
    return false;
  }

  @Override
  public boolean isUp() {
    return false;
  }

  @Override
  public boolean isDown() {
    return false;
  }

  @Override
  public boolean isShoot() {
    return false;
  }

  @Override
  public boolean isShoot2() {
    return false;
  }

  @Override
  public boolean isAbility() {
    return false;
  }

  @Override
  public TextureAtlas.AtlasRegion getInGameTex() {
    return myCursor;
  }

  @Override
  public void blur() {

  }
}
