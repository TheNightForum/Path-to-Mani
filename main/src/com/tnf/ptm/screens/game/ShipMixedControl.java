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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.input.Shooter;
import old.tnf.ptm.game.ship.PtmShip;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.input.Mover;
import com.tnf.ptm.screens.controlers.PtmInputManager;

import java.util.List;

public class ShipMixedControl implements ShipUiControl {
    public final PtmUiControl upCtrl;
    public final PtmUiControl shootCtrl;
    public final PtmUiControl shoot2Ctrl;
    public final PtmUiControl abilityCtrl;
    private final PtmUiControl myDownCtrl;
    private final Vector2 myMouseWorldPos;
    private final TextureAtlas.AtlasRegion myCursor;
    private boolean myRight;
    private boolean myLeft;

    ShipMixedControl(PtmApplication ptmApplication, List<PtmUiControl> controls) {
        GameOptions gameOptions = ptmApplication.getOptions();
        myCursor = ptmApplication.getTexMan().getTexture("ui/cursorTarget");
        myMouseWorldPos = new Vector2();
        upCtrl = new PtmUiControl(null, false, gameOptions.getKeyUpMouse());
        controls.add(upCtrl);
        myDownCtrl = new PtmUiControl(null, false, gameOptions.getKeyDownMouse());
        controls.add(myDownCtrl);
        shootCtrl = new PtmUiControl(null, false, gameOptions.getKeyShoot());
        controls.add(shootCtrl);
        shoot2Ctrl = new PtmUiControl(null, false, gameOptions.getKeyShoot2());
        controls.add(shoot2Ctrl);
        abilityCtrl = new PtmUiControl(null, false, gameOptions.getKeyAbility());
        controls.add(abilityCtrl);
    }

    @Override
    public void update(PtmApplication ptmApplication, boolean enabled) {
        GameOptions gameOptions = ptmApplication.getOptions();
        blur();
        if (!enabled) {
            return;
        }
        PtmInputManager im = ptmApplication.getInputMan();
        PtmGame game = ptmApplication.getGame();
        PtmShip hero = game.getHero();
        if (hero != null) {
            myMouseWorldPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.getCam().screenToWorld(myMouseWorldPos);
            float desiredAngle = PtmMath.angle(hero.getPosition(), myMouseWorldPos);
            Boolean ntt = Mover.needsToTurn(hero.getAngle(), desiredAngle, hero.getRotSpd(), hero.getRotAcc(), Shooter.MIN_SHOOT_AAD);
            if (ntt != null) {
                if (ntt) {
                    myRight = true;
                } else {
                    myLeft = true;
                }
            }
            if (!im.isMouseOnUi()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    shootCtrl.maybeFlashPressed(gameOptions.getKeyShoot());
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    shoot2Ctrl.maybeFlashPressed(gameOptions.getKeyShoot2());
                }
                if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                    abilityCtrl.maybeFlashPressed(gameOptions.getKeyAbility());
                }
            }
        }
    }

    @Override
    public boolean isLeft() {
        return myLeft;
    }

    @Override
    public boolean isRight() {
        return myRight;
    }

    @Override
    public boolean isUp() {
        return upCtrl.isOn();
    }

    @Override
    public boolean isDown() {
        return myDownCtrl.isOn();
    }

    @Override
    public boolean isShoot() {
        return shootCtrl.isOn();
    }

    @Override
    public boolean isShoot2() {
        return shoot2Ctrl.isOn();
    }

    @Override
    public boolean isAbility() {
        return abilityCtrl.isOn();
    }

    @Override
    public TextureAtlas.AtlasRegion getInGameTex() {
        return myCursor;
    }

    @Override
    public void blur() {
        myLeft = false;
        myRight = false;
    }
}
