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
package old.tnf.ptm.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.common.PtmColor;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.ui.PtmUiScreen;
import old.tnf.ptm.GameOptions;
import old.tnf.ptm.PtmApplication;
import old.tnf.ptm.assets.Assets;
import old.tnf.ptm.ui.FontSize;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Config Screen to Change Input Mapping</h1>
 * The input mapping screen is based on the inventory screen used within the game.
 */
public class InputMapScreen implements PtmUiScreen {
    private static final float IMG_COL_PERC = .1f;
    private static final float EQUI_COL_PERC = .1f;
    private static final float PRICE_COL_PERC = .1f;
    private static final float AMT_COL_PERC = .1f;
    private static final float SMALL_GAP = .004f;
    private static final float HEADER_TEXT_OFFSET = .005f;
    private static final int BUTTON_ROWS = 4;
    final InputMapKeyboardScreen inputMapKeyboardScreen;
    final InputMapControllerScreen inputMapControllerScreen;
    final InputMapMixedScreen inputMapMixedScreen;
    private final TextureAtlas.AtlasRegion bgTex;
    private final List<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl[] itemControls;
    private final PtmUiControl previousControl;
    private final PtmUiControl nextControl;
    private final PtmUiControl cancelControl;
    private final PtmUiControl saveControl;
    private final PtmUiControl defaultsControl;
    private final PtmUiControl upControl;
    private final PtmUiControl downControl;

    private final Vector2 listHeaderPos;
    private final Rectangle listArea;
    private final Rectangle detailsArea;
    private final Rectangle itemControlsArea;
    private InputMapOperations operations;
    private int page;
    private int selectedIndex;

    InputMapScreen(float resolutionRatio, GameOptions gameOptions) {
        float contentW = .8f;
        float col0 = resolutionRatio / 2 - contentW / 2;
        float row = 0.2f;
        float bigGap = SMALL_GAP * 6;
        float headerH = .03f;

        // List header & controls
        listHeaderPos = new Vector2(col0 + HEADER_TEXT_OFFSET, row + HEADER_TEXT_OFFSET); // offset hack
        float listCtrlW = contentW * .15f;
        Rectangle nextArea = new Rectangle(col0 + contentW - listCtrlW, row, listCtrlW, headerH);
        nextControl = new PtmUiControl(nextArea, true, gameOptions.getKeyRight());
        nextControl.setDisplayName(">");
        controls.add(nextControl);
        Rectangle prevArea = new Rectangle(nextArea.x - SMALL_GAP - listCtrlW, row, listCtrlW, headerH);
        previousControl = new PtmUiControl(prevArea, true, gameOptions.getKeyLeft());
        previousControl.setDisplayName("<");
        controls.add(previousControl);
        row += headerH + SMALL_GAP;

        // List
        float itemRowH = .04f;
        float listRow0 = row;
        itemControls = new PtmUiControl[Const.ITEM_GROUPS_PER_PAGE];
        for (int i = 0; i < Const.ITEM_GROUPS_PER_PAGE; i++) {
            Rectangle itemR = new Rectangle(col0, row, contentW, itemRowH);
            PtmUiControl itemCtrl = new PtmUiControl(itemR, true);
            itemControls[i] = itemCtrl;
            controls.add(itemCtrl);
            row += itemRowH + SMALL_GAP;
        }
        listArea = new Rectangle(col0, row, contentW, row - SMALL_GAP - listRow0);
        row += bigGap;

        // Detail header & area
        row += headerH + SMALL_GAP;
        float itemCtrlAreaW = contentW * .4f;
        itemControlsArea = new Rectangle(col0 + contentW - itemCtrlAreaW, row, itemCtrlAreaW, .2f);
        detailsArea = new Rectangle(col0, row, contentW - itemCtrlAreaW - SMALL_GAP, itemControlsArea.height);
        // row += detailsArea.height;

        // Add the buttons and controls
        cancelControl = new PtmUiControl(itemControlRectangle(3), true, gameOptions.getKeyClose());
        cancelControl.setDisplayName("Cancel");
        controls.add(cancelControl);

        saveControl = new PtmUiControl(itemControlRectangle(2), true);
        saveControl.setDisplayName("Save");
        controls.add(saveControl);

        defaultsControl = new PtmUiControl(itemControlRectangle(1), true);
        defaultsControl.setDisplayName("Defaults");
        controls.add(defaultsControl);

        upControl = new PtmUiControl(null, true, gameOptions.getKeyUp());
        controls.add(upControl);
        downControl = new PtmUiControl(null, true, gameOptions.getKeyDown());
        controls.add(downControl);

        // Create the input screens
        inputMapKeyboardScreen = new InputMapKeyboardScreen(this, gameOptions);
        inputMapControllerScreen = new InputMapControllerScreen();
        inputMapMixedScreen = new InputMapMixedScreen();

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication cmp, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        GameOptions gameOptions = cmp.getOptions();
        PtmInputManager im = cmp.getInputMan();
        MenuScreens screens = cmp.getMenuScreens();

        // Save - saves new settings and returns to the options screen
        if (saveControl.isJustOff()) {
            operations.save(gameOptions);
            im.setScreen(cmp, screens.options);
        }

        if (cancelControl.isJustOff()) {
            if (operations.isEnterNewKey()) {
                // Cancel - cancel the current key being entered
                operations.setEnterNewKey(false);
            } else {
                // Cancel - return to options screen without saving
                im.setScreen(cmp, screens.options);
            }
        }

        // Disable handling of key inputs while entering a new input key
        if (operations.isEnterNewKey()) {
            previousControl.setEnabled(false);
            nextControl.setEnabled(false);
            upControl.setEnabled(false);
            downControl.setEnabled(false);
            for (PtmUiControl itemControl : itemControls) {
                itemControl.setEnabled(false);
            }
            return;
        } else {
            upControl.setEnabled(true);
            downControl.setEnabled(true);
            for (PtmUiControl itemControl : itemControls) {
                itemControl.setEnabled(true);
            }
        }

        // Defaults - Reset the input keys back to their default values
        if (defaultsControl.isJustOff()) {
            operations.resetToDefaults(gameOptions);
        }

        // Selected Item Control
        List<InputConfigItem> itemsList = operations.getItems(gameOptions);
        int groupCount = itemsList.size();
        int pageCount = groupCount / Const.ITEM_GROUPS_PER_PAGE;

        // Select the item the mouse clicked
        int offset = page * Const.ITEM_GROUPS_PER_PAGE;
        for (int i = 0; i < itemControls.length; i++) {
            PtmUiControl itemCtrl = itemControls[i];
            if (itemCtrl.isJustOff()) {
                selectedIndex = i + offset;
                operations.setEnterNewKey(true);
            }
        }

        // Left and Right Page Control
        if (previousControl.isJustOff()) {
            page--;
        }
        if (nextControl.isJustOff()) {
            page++;
        }
        if (pageCount == 0 || pageCount * Const.ITEM_GROUPS_PER_PAGE < groupCount) {
            pageCount += 1;
        }
        if (page < 0) {
            page = 0;
        }
        if (page >= pageCount) {
            page = pageCount - 1;
        }
        previousControl.setEnabled(0 < page);
        nextControl.setEnabled(page < pageCount - 1);

        // Ensure Selected item is on page
        if (selectedIndex < offset || selectedIndex >= offset + Const.ITEM_GROUPS_PER_PAGE) {
            selectedIndex = offset;
        }

        // Up and Down Control
        if (upControl.isJustOff()) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = 0;
            }
            if (selectedIndex < offset) {
                page--;
            }
        }
        if (downControl.isJustOff()) {
            selectedIndex++;
            if (selectedIndex >= groupCount) {
                selectedIndex = groupCount - 1;
            }
            if (selectedIndex >= offset + Const.ITEM_GROUPS_PER_PAGE) {
                page++;
            }
            if (page >= pageCount) {
                page = pageCount - 1;
            }
        }

        // Inform the input screen which item is selected
        operations.setSelectedIndex(selectedIndex);
    }

    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, PtmColor.WHITE);
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, PtmApplication ptmApplication) {

    }

    @Override
    public void drawText(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        GameOptions gameOptions = ptmApplication.getOptions();
        List<InputConfigItem> list = operations.getItems(gameOptions);

        float imgColW = listArea.width * IMG_COL_PERC;
        float equiColW = listArea.width * EQUI_COL_PERC;
        float priceWidth = listArea.width * PRICE_COL_PERC;
        float amtWidth = listArea.width * AMT_COL_PERC;
        float nameWidth = listArea.width - imgColW - equiColW - priceWidth - amtWidth;

        // Display the input mapping in the grid control
        for (int i = 0; i < itemControls.length; i++) {
            int groupIdx = page * Const.ITEM_GROUPS_PER_PAGE + i;
            int groupCount = list.size();
            if (groupCount <= groupIdx) {
                continue;
            }
            PtmUiControl itemCtrl = itemControls[i];
            String displayName = list.get(groupIdx).getDisplayName();
            String inputKey = list.get(groupIdx).getInputKey();
            Rectangle rect = itemCtrl.getScreenArea();
            float rowCenterY = rect.y + rect.height / 2;

            // Draw the name of in the input and the key it is mapped to
            uiDrawer.drawString(displayName, rect.x + equiColW + imgColW + nameWidth / 2, rowCenterY, FontSize.WINDOW, true, selectedIndex == groupIdx ? PtmColor.WHITE : PtmColor.G);
            uiDrawer.drawString(inputKey, rect.x + rect.width - amtWidth - priceWidth / 2, rowCenterY, FontSize.WINDOW, true, PtmColor.LG);
        }

        // Draw the header title
        uiDrawer.drawString(operations.getHeader(), listHeaderPos.x, listHeaderPos.y, FontSize.WINDOW, false, PtmColor.WHITE);

        // Draw the detail text
        uiDrawer.drawString(operations.getDisplayDetail(), detailsArea.x + .015f, detailsArea.y + .015f, FontSize.WINDOW, false, PtmColor.WHITE);
    }

    @Override
    public boolean reactsToClickOutside() {
        return false;
    }

    @Override
    public boolean isCursorOnBg(PtmInputManager.InputPointer inputPointer) {
        return false;
    }

    @Override
    public void onAdd(PtmApplication ptmApplication) {
        // Add any extra screen information as required by the input screens. E.g. buttons
        if (operations != null) {
            ptmApplication.getInputMan().addScreen(ptmApplication, operations);
        }

        page = 0;
        selectedIndex = 0;
    }

    @Override
    public void blurCustom(PtmApplication cmp) {
    }

    private Rectangle itemControlRectangle(int row) {
        float h = (itemControlsArea.height - SMALL_GAP * (BUTTON_ROWS - 1)) / BUTTON_ROWS;
        return new Rectangle(itemControlsArea.x, itemControlsArea.y + (h + SMALL_GAP) * row, itemControlsArea.width, h);
    }

    void setOperations(InputMapOperations operations) {
        this.operations = operations;
    }
}
