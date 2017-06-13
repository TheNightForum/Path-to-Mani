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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.TextureManager;
import old.tnf.ptm.assets.audio.PlayableSound;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.common.PtmColor;
import old.tnf.ptm.game.PtmGame;
import old.tnf.ptm.game.sound.OggSoundManager;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;

public class PtmInputManager {
    private static final float CURSOR_SZ = .07f;
    private static final float WARN_PERC_GROWTH_TIME = 1f;
    private static final int POINTER_COUNT = 4;
    private static final float CURSOR_SHOW_TIME = 3;
    private static final float initialRatio = ((float) Gdx.graphics.getWidth()) / ((float) Gdx.graphics.getHeight());

    private static Cursor hiddenCursor;

    private final List<PtmUiScreen> screens = new ArrayList<>();
    private final List<PtmUiScreen> screenToRemove = new ArrayList<>();
    private final List<PtmUiScreen> screensToAdd = new ArrayList<>();
    private final InputPointer[] inputPointers;
    private final InputPointer flashInputPointer;
    private final Vector2 mousePos;
    private final Vector2 mousePrevPos;
    private final PlayableSound hoverSound;
    private final TextureAtlas.AtlasRegion uiCursor;
    private final Color warnColor;
    private float mouseIdleTime;
    private TextureAtlas.AtlasRegion currCursor;
    private boolean mouseOnUi;
    private float warnPerc;
    private boolean warnPercGrows;
    private Boolean scrolledUp;

    public PtmInputManager(TextureManager textureManager, OggSoundManager soundManager) {
        inputPointers = new InputPointer[POINTER_COUNT];
        for (int i = 0; i < POINTER_COUNT; i++) {
            inputPointers[i] = new InputPointer();
        }
        PtmInputProcessor sip = new PtmInputProcessor(this);
        Gdx.input.setInputProcessor(sip);
        flashInputPointer = new InputPointer();
        mousePos = new Vector2();
        mousePrevPos = new Vector2();

        // Create an empty 1x1 pixmap to use as hidden cursor
        Pixmap pixmap = new Pixmap(1, 1, RGBA8888);
        hiddenCursor = Gdx.graphics.newCursor(pixmap, 0, 0);
        pixmap.dispose();

        // We want the original mouse cursor to be hidden as we draw our own mouse cursor.
        Gdx.input.setCursorCatched(false);
        setMouseCursorHidden();
        uiCursor = textureManager.getTexture("ui/cursor");
        warnColor = new Color(PtmColor.UI_WARN);

        hoverSound = soundManager.getSound("engine:uiHover");
    }

    private static void setPointerPosition(InputPointer inputPointer, int screenX, int screenY) {
        int h = Gdx.graphics.getHeight();
        float currentRatio = ((float) Gdx.graphics.getWidth()) / ((float) Gdx.graphics.getHeight());

        inputPointer.x = 1f * screenX / h * (initialRatio / currentRatio);
        inputPointer.y = 1f * screenY / h;
    }

    /**
     * Hides the mouse cursor by setting it to a transparent image.
     */
    private void setMouseCursorHidden() {
        Gdx.graphics.setCursor(hiddenCursor);
    }

    void maybeFlashPressed(int keyCode) {
        for (PtmUiScreen screen : screens) {
            boolean consumed = false;
            List<PtmUiControl> controls = screen.getControls();
            for (PtmUiControl control : controls) {
                if (control.maybeFlashPressed(keyCode)) {
                    consumed = true;
                }
            }
            if (consumed) {
                return;
            }
        }

    }

    void maybeFlashPressed(int x, int y) {
        setPointerPosition(flashInputPointer, x, y);
        for (PtmUiScreen screen : screens) {
            List<PtmUiControl> controls = screen.getControls();
            for (PtmUiControl control : controls) {
                if (control.maybeFlashPressed(flashInputPointer)) {
                    return;
                }
            }
            if (screen.isCursorOnBg(flashInputPointer)) {
                return;
            }
        }

    }

    public void setScreen(PtmApplication ptmApplication, PtmUiScreen screen) {
        for (PtmUiScreen oldScreen : screens) {
            removeScreen(oldScreen, ptmApplication);
        }
        addScreen(ptmApplication, screen);
    }

    public void addScreen(PtmApplication ptmApplication, PtmUiScreen screen) {
        screensToAdd.add(screen);
        screen.onAdd(ptmApplication);
    }

    private void removeScreen(PtmUiScreen screen, PtmApplication ptmApplication) {
        screenToRemove.add(screen);
        List<PtmUiControl> controls = screen.getControls();
        for (PtmUiControl control : controls) {
            control.blur();
        }
        screen.blurCustom(ptmApplication);
    }

    public boolean isScreenOn(PtmUiScreen screen) {
        return screens.contains(screen);
    }

    public void update(PtmApplication ptmApplication) {
        boolean mobile = ptmApplication.isMobile();
        PtmGame game = ptmApplication.getGame();

        // This keeps the mouse within the window, but only when playing the game with the mouse.
        // All other times the mouse can freely leave and return.
        if (!mobile && (ptmApplication.getOptions().controlType == GameOptions.CONTROL_MIXED || ptmApplication.getOptions().controlType == GameOptions.CONTROL_MOUSE) &&
            game != null && getTopScreen() != game.getScreens().menuScreen) {
            if (!Gdx.input.isCursorCatched()) {
                Gdx.input.setCursorCatched(true);
            }
            maybeFixMousePos();
        } else {
            if (Gdx.input.isCursorCatched()) {
                Gdx.input.setCursorCatched(false);
            }
        }

        updatePointers();

        boolean consumed = false;
        mouseOnUi = false;
        boolean clickOutsideReacted = false;
        for (PtmUiScreen screen : screens) {
            boolean consumedNow = false;
            List<PtmUiControl> controls = screen.getControls();
            for (PtmUiControl control : controls) {
                control.update(inputPointers, currCursor != null, !consumed, this, ptmApplication);
                if (control.isOn() || control.isJustOff()) {
                    consumedNow = true;
                }
                Rectangle area = control.getScreenArea();
                if (area != null && area.contains(mousePos)) {
                    mouseOnUi = true;
                }
            }
            if (consumedNow) {
                consumed = true;
            }
            boolean clickedOutside = false;
            if (!consumed) {
                for (InputPointer inputPointer : inputPointers) {
                    boolean onBg = screen.isCursorOnBg(inputPointer);
                    if (inputPointer.pressed && onBg) {
                        clickedOutside = false;
                        consumed = true;
                        break;
                    }
                    if (!onBg && inputPointer.isJustUnPressed() && !clickOutsideReacted) {
                        clickedOutside = true;
                    }
                }
            }
            if (clickedOutside && screen.reactsToClickOutside()) {
                clickOutsideReacted = true;
            }
            if (screen.isCursorOnBg(inputPointers[0])) {
                mouseOnUi = true;
            }
            screen.updateCustom(ptmApplication, inputPointers, clickedOutside);
        }

        TutorialManager tutorialManager = game == null ? null : game.getTutMan();
        if (tutorialManager != null && tutorialManager.isFinished()) {
            ptmApplication.finishGame();
        }

        updateCursor(ptmApplication);
        addRemoveScreens();
        updateWarnPerc();
        scrolledUp = null;
    }

    private void updateWarnPerc() {
        float dif = PtmMath.toInt(warnPercGrows) * Const.REAL_TIME_STEP / WARN_PERC_GROWTH_TIME;
        warnPerc += dif;
        if (warnPerc < 0 || 1 < warnPerc) {
            warnPerc = PtmMath.clamp(warnPerc);
            warnPercGrows = !warnPercGrows;
        }
        warnColor.a = warnPerc * .5f;
    }

    private void addRemoveScreens() {
        for (PtmUiScreen screen : screenToRemove) {
            screens.remove(screen);
        }
        screenToRemove.clear();

        for (PtmUiScreen screen : screensToAdd) {
            if (isScreenOn(screen)) {
                continue;
            }
            screens.add(0, screen);
        }
        screensToAdd.clear();
    }

    private void updateCursor(PtmApplication ptmApplication) {
        if (ptmApplication.isMobile()) {
            return;
        }
        PtmGame game = ptmApplication.getGame();

        mousePos.set(inputPointers[0].x, inputPointers[0].y);
        if (ptmApplication.getOptions().controlType == GameOptions.CONTROL_MIXED || ptmApplication.getOptions().controlType == GameOptions.CONTROL_MOUSE) {
            if (game == null || mouseOnUi) {
                currCursor = uiCursor;
            } else {
                currCursor = game.getScreens().mainScreen.shipControl.getInGameTex();
                if (currCursor == null) {
                    currCursor = uiCursor;
                }
            }
            return;
        }
        if (mousePrevPos.epsilonEquals(mousePos, 0) && game != null && getTopScreen() != game.getScreens().menuScreen) {
            mouseIdleTime += Const.REAL_TIME_STEP;
            currCursor = mouseIdleTime < CURSOR_SHOW_TIME ? uiCursor : null;
        } else {
            currCursor = uiCursor;
            mouseIdleTime = 0;
            mousePrevPos.set(mousePos);
        }
    }

    private void maybeFixMousePos() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        mouseX = (int) PtmMath.clamp(mouseX, 0, w);
        mouseY = (int) PtmMath.clamp(mouseY, 0, h);
        Gdx.input.setCursorPosition(mouseX, mouseY);
    }

    private void updatePointers() {
        for (int i = 0; i < POINTER_COUNT; i++) {
            InputPointer inputPointer = inputPointers[i];
            int screenX = Gdx.input.getX(i);
            int screenY = Gdx.input.getY(i);
            setPointerPosition(inputPointer, screenX, screenY);
            inputPointer.prevPressed = inputPointer.pressed;
            inputPointer.pressed = Gdx.input.isTouched(i);
        }
    }

    public void draw(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        for (int i = screens.size() - 1; i >= 0; i--) {
            PtmUiScreen screen = screens.get(i);

            uiDrawer.setTextMode(false);
            screen.drawBg(uiDrawer, ptmApplication);
            List<PtmUiControl> controls = screen.getControls();
            for (PtmUiControl control : controls) {
                control.drawButton(uiDrawer, ptmApplication, warnColor);
            }
            screen.drawImgs(uiDrawer, ptmApplication);

            uiDrawer.setTextMode(true);
            screen.drawText(uiDrawer, ptmApplication);
            for (PtmUiControl control : controls) {
                control.drawDisplayName(uiDrawer);
            }
        }
        uiDrawer.setTextMode(null);

        PtmGame game = ptmApplication.getGame();
        TutorialManager tutorialManager = game == null ? null : game.getTutMan();
        if (tutorialManager != null && getTopScreen() != game.getScreens().menuScreen) {
            tutorialManager.draw(uiDrawer);
        }

        if (currCursor != null) {
            uiDrawer.draw(currCursor, CURSOR_SZ, CURSOR_SZ, CURSOR_SZ / 2, CURSOR_SZ / 2, mousePos.x, mousePos.y, 0, PtmColor.WHITE);
        }
    }

    public Vector2 getMousePos() {
        return mousePos;
    }

    public InputPointer[] getPtrs() {
        return inputPointers;
    }

    public boolean isMouseOnUi() {
        return mouseOnUi;
    }

    public void playHover(PtmApplication ptmApplication) {
        hoverSound.getOggSound().getSound().play(.7f * ptmApplication.getOptions().sfxVolumeMultiplier, .7f, 0);
    }

    public void playClick(PtmApplication ptmApplication) {
        hoverSound.getOggSound().getSound().play(.7f * ptmApplication.getOptions().sfxVolumeMultiplier, .9f, 0);
    }

    public PtmUiScreen getTopScreen() {
        return screens.isEmpty() ? null : screens.get(0);
    }

    public void scrolled(boolean up) {
        scrolledUp = up;
    }

    public Boolean getScrolledUp() {
        return scrolledUp;
    }

    public void dispose() {
        hoverSound.getOggSound().getSound().dispose();
    }

    public static class InputPointer {
        public float x;
        public float y;
        public boolean pressed;
        public boolean prevPressed;

        public boolean isJustPressed() {
            return pressed && !prevPressed;
        }

        public boolean isJustUnPressed() {
            return !pressed && prevPressed;
        }
    }

}
