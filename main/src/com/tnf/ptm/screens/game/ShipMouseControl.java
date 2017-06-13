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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.ship.PtmShip;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.game.BeaconHandler;

public class ShipMouseControl implements ShipUiControl {
    private final TextureAtlas.AtlasRegion myMoveCursor;
    private final TextureAtlas.AtlasRegion myAttackCursor;
    private final TextureAtlas.AtlasRegion myFollowCursor;
    private final Vector2 myMouseWorldPos;

    private TextureAtlas.AtlasRegion myCursor;

    ShipMouseControl(PtmApplication cmp) {
        myMoveCursor = cmp.getTexMan().getTexture("ui/cursorMove");
        myAttackCursor = cmp.getTexMan().getTexture("ui/cursorAttack");
        myFollowCursor = cmp.getTexMan().getTexture("ui/cursorFollow");
        myMouseWorldPos = new Vector2();
    }

    @Override
    public void update(PtmApplication ptmApplication, boolean enabled) {
        PtmGame g = ptmApplication.getGame();
        PtmShip h = g.getHero();
        myCursor = null;
        if (h != null) {
            myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
            g.getCam().screenToWorld(myMouseWorldPos);
            PtmInputManager im = ptmApplication.getInputMan();
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
}
