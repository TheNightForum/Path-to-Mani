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
package com.tnf.ptm.screens.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.tnf.ptm.common.Const;
import com.tnf.ptm.common.PtmMath;
import com.tnf.ptm.screens.controlers.PtmInputManager;
import com.tnf.ptm.screens.controlers.PtmUiControl;
import com.tnf.ptm.screens.controlers.PtmUiScreen;
import com.tnf.ptm.common.GameOptions;
import com.tnf.ptm.PtmApplication;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.screens.controlers.FontSize;
import com.tnf.ptm.screens.controlers.UiDrawer;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class CreditsScreen implements PtmUiScreen {
    private static final float MAX_AWAIT = 6f;
    private final TextureAtlas.AtlasRegion bgTex;
    private final ArrayList<PtmUiControl> controls = new ArrayList<>();
    private final PtmUiControl closeControl;

    private final ArrayList<String> myPages = new ArrayList<>();
    private final Color myColor;
    private int pageIndex;
    private float pageProgressPercent;

    CreditsScreen(float resolutionRatio, GameOptions gameOptions) {
        closeControl = new PtmUiControl(MenuLayout.bottomRightFloatingButton(resolutionRatio), true, gameOptions.getKeyEscape());
        closeControl.setDisplayName("Close");
        controls.add(closeControl);
        myColor = PtmColor.col(1, 1);

        String[][] sss = {
                {
                        "A game from",
                        "",
                        "TheNightForum"
                },
                {
                        "Original Creators",
                        "",
                        "Idea, coding, team lead:",
                        "Milosh Petrov",
                        "",
                        "Drawing:",
                        "Kent C. Jensen",
                        "",
                        "Additional coding:",
                        "Nika \"NoiseDoll\" Burimenko",
                        "",
                        "Additional drawing:",
                        "Julia Nikolaeva"
                },
                {
                        "Contributors on GitHub",
                        "",
                        "Cervator, Rulasmur",
                        "theotherjay, LinusVanElswijk",
                        "SimonC4, grauerkoala, rzats",
                        "LadySerenaKitty, askneller",
                        "JGelfand, AvaLanCS, scirelli",
                        "Sigma-One, vampcat"
                },
                {
                        "Soundtrack by NeonInsect"
                },
                {
                        "Game engine:",
                        "LibGDX",
                        "",
                        "Windows wrapper:",
                        "Launch4J"
                },
                {
                        "Font:",
                        "\"Jet Set\" by Captain Falcon",
                        "",
                        "Sounds by FreeSound.org users:",
                        "Smokum, Mattpavone",
                        "Hanstimm, Sonidor,",
                        "Isaac200000, TheHadnot, Garzul",
                        "Dpoggioli, Raremess, Giddykipper,",
                        "Steveygos93",
                },
        };
        for (String[] ss : sss) {
            StringBuilder page = new StringBuilder();
            for (String s : ss) {
                page.append(s).append("\n");
            }
            myPages.add(page.toString());
        }

        bgTex = Assets.getAtlasRegion(new ResourceUrn("engine:mainMenuBg"), Texture.TextureFilter.Linear);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void onAdd(PtmApplication ptmApplication) {
        pageIndex = 0;
        pageProgressPercent = 0;
        myColor.a = 0;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (closeControl.isJustOff()) {
            ptmApplication.getInputMan().setScreen(ptmApplication, ptmApplication.getMenuScreens().main);
            return;
        }
        pageProgressPercent += Const.REAL_TIME_STEP / MAX_AWAIT;
        if (pageProgressPercent > 1) {
            pageProgressPercent = 0;
            pageIndex++;
            if (pageIndex >= myPages.size()) {
                pageIndex = 0;
            }
        }
        float a = pageProgressPercent * 2;
        if (a > 1) {
            a = 2 - a;
        }
        a *= 3;
        myColor.a = PtmMath.clamp(a);
    }

    @Override
    public void drawBg(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.draw(bgTex, uiDrawer.r, 1, uiDrawer.r / 2, 0.5f, uiDrawer.r / 2, 0.5f, 0, PtmColor.WHITE);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        uiDrawer.drawString(myPages.get(pageIndex), uiDrawer.r / 2, .5f, FontSize.MENU, true, myColor);
    }
}
