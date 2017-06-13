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
package old.tnf.ptm.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import old.tnf.ptm.Const;
import old.tnf.ptm.TextureManager;
import com.tnf.ptm.common.PtmColor;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.game.*;
import old.tnf.ptm.game.planet.Planet;
import old.tnf.ptm.game.ship.PtmShip;
import old.tnf.ptm.ui.PtmUiControl;
import old.tnf.ptm.ui.PtmUiScreen;
import old.tnf.ptm.GameOptions;
import com.tnf.ptm.PtmApplication;
import old.tnf.ptm.TextAlignment;
import old.tnf.ptm.game.PtmObject;
import old.tnf.ptm.game.item.Gun;
import old.tnf.ptm.game.item.ItemManager;
import old.tnf.ptm.game.item.Shield;
import old.tnf.ptm.game.item.PtmItem;
import old.tnf.ptm.game.ship.ShipAbility;
import old.tnf.ptm.ui.FontSize;
import old.tnf.ptm.ui.PtmInputManager;
import old.tnf.ptm.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MainScreen implements PtmUiScreen {
    // TODO: Rename!
    private static final float ICON_SZ = .03f;
    private static final float BAR_SZ = ICON_SZ * 5;
    private static final int MAX_ICON_COUNT = 3;
    static final float CELL_SZ = .17f;
    private static final float H_PAD = .005f;
    private static final float V_PAD = H_PAD;
    static final float HELPER_ROW_1 = 1 - 3f * CELL_SZ;
    private static final float HELPER_ROW_2 = HELPER_ROW_1 - .5f * CELL_SZ;

    private final List<PtmUiControl> controls = new ArrayList<>();
    public final ShipUiControl shipControl;
    public final PtmUiControl mapControl;
    public final PtmUiControl inventoryControl;
    public final PtmUiControl talkControl;
    private final PtmUiControl menuControl;
    private final PtmUiControl pauseControl;

    private final ZoneNameAnnouncer zoneNameAnnouncer;
    private final BorderDrawer borderDrawer;
    private final List<WarnDrawer> warnDrawers = new ArrayList<>();

    private final TextureAtlas.AtlasRegion lifeTex;
    private final TextureAtlas.AtlasRegion infinityTex;
    private final TextureAtlas.AtlasRegion waitTex;
    private final TextureAtlas.AtlasRegion compassTex;

    private final Color myCompassTint;
    private final TextPlace myLifeTp;
    private final TextPlace myRepairsExcessTp;
    private final TextPlace myShieldLifeTp;
    private final TextPlace myG1AmmoTp;
    private final TextPlace myG1AmmoExcessTp;
    private final TextPlace myG2AmmoTp;
    private final TextPlace myG2AmmoExcessTp;
    private final TextPlace myChargesExcessTp;
    private final TextPlace myMoneyExcessTp;

    MainScreen(float resolutionRatio, RightPaneLayout rightPaneLayout, PtmApplication ptmApplication) {
        GameOptions gameOptions = ptmApplication.getOptions();

        int ct = ptmApplication.getOptions().controlType;
        if (ct == GameOptions.CONTROL_KB) {
            shipControl = new ShipKbControl(ptmApplication, resolutionRatio, controls);
        } else if (ct == GameOptions.CONTROL_MIXED) {
            shipControl = new ShipMixedControl(ptmApplication, controls);
        } else if (ct == GameOptions.CONTROL_MOUSE) {
            shipControl = new ShipMouseControl(ptmApplication);
        } else {
            shipControl = new ShipControllerControl(ptmApplication);
        }
        boolean mobile = ptmApplication.isMobile();
        float lastCol = resolutionRatio - MainScreen.CELL_SZ;
        Rectangle menuArea = mobile ? btn(0, HELPER_ROW_2, true) : rightPaneLayout.buttonRect(0);
        menuControl = new PtmUiControl(menuArea, true, gameOptions.getKeyMenu());
        menuControl.setDisplayName("Menu");
        controls.add(menuControl);
        Rectangle mapArea = mobile ? btn(0, HELPER_ROW_1, true) : rightPaneLayout.buttonRect(1);
        mapControl = new PtmUiControl(mapArea, true, gameOptions.getKeyMap());
        mapControl.setDisplayName("Map");
        controls.add(mapControl);
        Rectangle invArea = mobile ? btn(lastCol, HELPER_ROW_1, true) : rightPaneLayout.buttonRect(2);
        inventoryControl = new PtmUiControl(invArea, true, gameOptions.getKeyInventory());
        inventoryControl.setDisplayName("Items");
        controls.add(inventoryControl);
        Rectangle talkArea = mobile ? btn(lastCol, HELPER_ROW_2, true) : rightPaneLayout.buttonRect(3);
        talkControl = new PtmUiControl(talkArea, true, gameOptions.getKeyTalk());
        talkControl.setDisplayName("Talk");
        controls.add(talkControl);
        pauseControl = new PtmUiControl(null, true, gameOptions.getKeyPause());
        controls.add(pauseControl);

        warnDrawers.add(new CollisionWarnDrawer(resolutionRatio));
        warnDrawers.add(new SunWarnDrawer(resolutionRatio));
        warnDrawers.add(new EnemyWarn(resolutionRatio));
        warnDrawers.add(new DmgWarnDrawer(resolutionRatio));
        warnDrawers.add(new NoShieldWarn(resolutionRatio));
        warnDrawers.add(new NoArmorWarn(resolutionRatio));

        zoneNameAnnouncer = new ZoneNameAnnouncer();
        borderDrawer = new BorderDrawer(resolutionRatio, ptmApplication);

        TextureManager textureManager = ptmApplication.getTexMan();
        lifeTex = textureManager.getTexture(TextureManager.ICONS_DIR + "life");
        infinityTex = textureManager.getTexture(TextureManager.ICONS_DIR + "infinity");
        waitTex = textureManager.getTexture(TextureManager.ICONS_DIR + "wait");
        compassTex = textureManager.getTexture("ui/compass");
        myCompassTint = PtmColor.col(1, 0);

        myLifeTp = new TextPlace(PtmColor.W50);
        myRepairsExcessTp = new TextPlace(PtmColor.WHITE);
        myShieldLifeTp = new TextPlace(PtmColor.W50);
        myG1AmmoTp = new TextPlace(PtmColor.W50);
        myG1AmmoExcessTp = new TextPlace(PtmColor.WHITE);
        myG2AmmoTp = new TextPlace(PtmColor.W50);
        myG2AmmoExcessTp = new TextPlace(PtmColor.WHITE);
        myChargesExcessTp = new TextPlace(PtmColor.WHITE);
        myMoneyExcessTp = new TextPlace(PtmColor.WHITE);
    }

    public static Rectangle btn(float x, float y, boolean halfHeight) {
        float gap = .01f;
        float cellH = CELL_SZ;
        if (halfHeight) {
            cellH /= 2;
        }
        return new Rectangle(x + gap, y + gap, CELL_SZ - gap * 2, cellH - gap * 2);
    }

    public void maybeDrawHeight(UiDrawer drawer, PtmApplication ptmApplication) {
        PtmGame game = ptmApplication.getGame();
        Planet np = game.getPlanetMan().getNearestPlanet();
        PtmCam cam = game.getCam();
        Vector2 camPos = cam.getPos();
        if (np != null && np.getPos().dst(camPos) < np.getFullHeight()) {
            drawHeight(drawer, np, camPos, cam.getAngle());
        }
    }

    private void drawHeight(UiDrawer drawer, Planet np, Vector2 camPos, float camAngle) {
        float toPlanet = camPos.dst(np.getPos());
        toPlanet -= np.getGroundHeight();
        if (Const.ATM_HEIGHT < toPlanet) {
            return;
        }
        float perc = toPlanet / Const.ATM_HEIGHT;
        float sz = .08f;
        float maxY = 1 - sz / 2;
        float y = 1 - perc;
        myCompassTint.a = PtmMath.clamp(1.5f * y);
        if (maxY < y) {
            y = maxY;
        }
        float angle = np.getAngle() - camAngle;
        drawer.draw(compassTex, sz, sz, sz / 2, sz / 2, sz / 2, y, angle, myCompassTint);
    }

    @Override
    public List<PtmUiControl> getControls() {
        return controls;
    }

    @Override
    public void updateCustom(PtmApplication ptmApplication, PtmInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        if (DebugOptions.PRINT_BALANCE) {
            ptmApplication.finishGame();
            return;
        }
        PtmGame game = ptmApplication.getGame();
        PtmInputManager inputMan = ptmApplication.getInputMan();
        GameScreens screens = game.getScreens();
        PtmShip hero = game.getHero();

        for (WarnDrawer warnDrawer : warnDrawers) {
            warnDrawer.update(game);
        }

        zoneNameAnnouncer.update(game);

        if (menuControl.isJustOff()) {
            inputMan.setScreen(ptmApplication, screens.menuScreen);
        }

        boolean controlsEnabled = inputMan.getTopScreen() == this;
        shipControl.update(ptmApplication, controlsEnabled);

        if (mapControl.isJustOff()) {
            inputMan.setScreen(ptmApplication, screens.mapScreen);
        }

        inventoryControl.setEnabled(hero != null);
        if (hero != null && !inputMan.isScreenOn(screens.inventoryScreen)) {
            if (hero.getItemContainer().hasNew()) {
                inventoryControl.enableWarn();
            }
        }
        if (inventoryControl.isJustOff()) {
            InventoryScreen is = screens.inventoryScreen;
            boolean isOn = inputMan.isScreenOn(is);
            inputMan.setScreen(ptmApplication, screens.mainScreen);
            if (!isOn) {
                is.setOperations(is.showInventory);
                inputMan.addScreen(ptmApplication, is);
            }
        }

        updateTalk(game);

        if (pauseControl.isJustOff()) {
            game.setPaused(!game.isPaused());
        }
    }

    private void updateTalk(PtmGame game) {
        PtmShip hero = game.getHero();
        if (hero == null) {
            talkControl.setEnabled(false);
            return;
        }
        FactionManager factionManager = game.getFactionMan();

        PtmShip target = null;
        float minDist = TalkScreen.MAX_TALK_DIST;
        float har = hero.getHull().config.getApproxRadius();
        List<PtmObject> objs = game.getObjMan().getObjs();
        for (PtmObject o : objs) {
            if (!(o instanceof PtmShip)) {
                continue;
            }
            PtmShip ship = (PtmShip) o;
            if (factionManager.areEnemies(hero, ship)) {
                continue;
            }
            if (ship.getTradeContainer() == null) {
                continue;
            }
            float dst = ship.getPosition().dst(hero.getPosition());
            float ar = ship.getHull().config.getApproxRadius();
            if (minDist < dst - har - ar) {
                continue;
            }
            target = ship;
            minDist = dst;
        }
        talkControl.setEnabled(target != null);
        if (talkControl.isJustOff()) {
            TalkScreen talkScreen = game.getScreens().talkScreen;
            PtmApplication cmp = game.getCmp();
            PtmInputManager inputMan = cmp.getInputMan();
            boolean isOn = inputMan.isScreenOn(talkScreen);
            inputMan.setScreen(cmp, this);
            if (!isOn) {
                talkScreen.setTarget(target);
                inputMan.addScreen(cmp, talkScreen);
            }
        }
    }

    private boolean drawGunStat(UiDrawer uiDrawer, PtmShip hero, boolean secondary, float col0, float col1,
                                float col2, float y) {
        Gun g = hero.getHull().getGun(secondary);
        if (g == null) {
            return false;
        }
        TextureAtlas.AtlasRegion tex = g.config.icon;

        uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, col0, y, 0, PtmColor.WHITE);
        float curr;
        float max;
        if (g.reloadAwait > 0) {
            max = g.config.reloadTime;
            curr = max - g.reloadAwait;
        } else {
            curr = g.ammo;
            max = g.config.clipConf.size;
        }
        TextPlace ammoTp = g.reloadAwait > 0 ? null : secondary ? myG2AmmoTp : myG1AmmoTp;
        drawBar(uiDrawer, col1, y, curr, max, ammoTp);
        if (g.reloadAwait > 0) {
            drawWait(uiDrawer, col1, y);
        }
        if (!g.config.clipConf.infinite) {
            int clipCount = hero.getItemContainer().count(g.config.clipConf.example);
            drawIcons(uiDrawer, col2, y, clipCount, g.config.clipConf.icon, secondary ? myG2AmmoExcessTp : myG1AmmoExcessTp);
        } else {
            uiDrawer.draw(infinityTex, ICON_SZ, ICON_SZ, 0, 0, col2, y, 0, PtmColor.WHITE);
        }
        return true;
    }

    private void drawWait(UiDrawer uiDrawer, float x, float y) {
        uiDrawer.draw(waitTex, ICON_SZ, ICON_SZ, ICON_SZ / 2, ICON_SZ / 2, x + BAR_SZ / 2, y + ICON_SZ / 2, 0, PtmColor.WHITE);
    }

    private void drawBar(UiDrawer uiDrawer, float x, float y, float curr, float max, TextPlace tp) {
        float perc = curr / max;
        uiDrawer.draw(uiDrawer.whiteTex, BAR_SZ, ICON_SZ, 0, 0, x, y, 0, PtmColor.UI_DARK);
        uiDrawer.draw(uiDrawer.whiteTex, BAR_SZ * perc, ICON_SZ, 0, 0, x, y, 0, PtmColor.UI_LIGHT);
        if (tp != null && max > 1 && curr > 0) {
            tp.text = (int) curr + "/" + (int) max;
            tp.pos.set(x + BAR_SZ / 2, y + ICON_SZ / 2);
        }
    }

    private void drawIcons(UiDrawer uiDrawer, float x, float y, int count, TextureAtlas.AtlasRegion tex,
                           TextPlace textPlace) {
        int excess = count - MAX_ICON_COUNT;
        int iconCount = excess > 0 ? MAX_ICON_COUNT : count;
        for (int i = 0; i < iconCount; i++) {
            uiDrawer.draw(tex, ICON_SZ, ICON_SZ, 0, 0, x, y, 0, PtmColor.WHITE);
            x += ICON_SZ + H_PAD;
        }
        if (excess > 0) {
            updateTextPlace(x, y, "+" + excess, textPlace);
        }
    }

    private void updateTextPlace(float x, float y, String text, TextPlace textPlace) {
        textPlace.text = text;
        textPlace.pos.set(x + ICON_SZ / 2, y + ICON_SZ / 2);
    }

    @Override
    public void drawImgs(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        myLifeTp.text = null;
        myRepairsExcessTp.text = null;
        myShieldLifeTp.text = null;
        myG1AmmoTp.text = null;
        myG1AmmoExcessTp.text = null;
        myG2AmmoTp.text = null;
        myG2AmmoExcessTp.text = null;
        myChargesExcessTp.text = null;
        myMoneyExcessTp.text = null;

        maybeDrawHeight(uiDrawer, ptmApplication);
        borderDrawer.draw(uiDrawer, ptmApplication);

        PtmGame game = ptmApplication.getGame();
        PtmShip hero = game.getHero();
        if (hero != null) {
            float row = BorderDrawer.TISHCH_SZ + V_PAD;
            float col0 = BorderDrawer.TISHCH_SZ + H_PAD;
            float col1 = col0 + ICON_SZ + H_PAD;
            float col2 = col1 + BAR_SZ + H_PAD;

            Shield shield = hero.getShield();
            if (shield != null) {
                uiDrawer.draw(shield.getIcon(game), ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, PtmColor.WHITE);
                drawBar(uiDrawer, col1, row, shield.getLife(), shield.getMaxLife(), myShieldLifeTp);
                row += ICON_SZ + V_PAD;
            }

            uiDrawer.draw(lifeTex, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, PtmColor.WHITE);
            drawBar(uiDrawer, col1, row, hero.getLife(), hero.getHull().config.getMaxLife(), myLifeTp);
            int repairKitCount = hero.getItemContainer().count(game.getItemMan().getRepairExample());
            ItemManager itemManager = game.getItemMan();
            drawIcons(uiDrawer, col2, row, repairKitCount, itemManager.repairIcon, myRepairsExcessTp);

            row += ICON_SZ + V_PAD;
            boolean consumed = drawGunStat(uiDrawer, hero, false, col0, col1, col2, row);
            if (consumed) {
                row += ICON_SZ + V_PAD;
            }
            consumed = drawGunStat(uiDrawer, hero, true, col0, col1, col2, row);
            if (consumed) {
                row += ICON_SZ + V_PAD;
            }

            ShipAbility ability = hero.getAbility();
            PtmItem abilityChargeEx = ability == null ? null : ability.getConfig().getChargeExample();
            if (abilityChargeEx != null) {
                int abilityChargeCount = hero.getItemContainer().count(abilityChargeEx);
                TextureAtlas.AtlasRegion icon = abilityChargeEx.getIcon(game);
                uiDrawer.draw(icon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, PtmColor.WHITE);
                float chargePerc = 1 - PtmMath.clamp(hero.getAbilityAwait() / ability.getConfig().getRechargeTime());
                drawBar(uiDrawer, col1, row, chargePerc, 1, null);
                if (chargePerc < 1) {
                    drawWait(uiDrawer, col1, row);
                }
                drawIcons(uiDrawer, col2, row, abilityChargeCount, icon, myChargesExcessTp);
                row += ICON_SZ + V_PAD;
            }
            uiDrawer.draw(game.getItemMan().moneyIcon, ICON_SZ, ICON_SZ, 0, 0, col0, row, 0, PtmColor.WHITE);
            myMoneyExcessTp.text = Integer.toString(Math.round(hero.getMoney()));
            myMoneyExcessTp.pos.set(col1, row + ICON_SZ / 2);
            //updateTextPlace(col1, row, (int) hero.getMoney() + "", myMoneyExcessTp);
        }

        for (int i = 0, sz = warnDrawers.size(); i < sz; i++) {
            WarnDrawer wd = warnDrawers.get(i);
            if (wd.drawPerc > 0) {
                wd.draw(uiDrawer);
                break;
            }
        }
    }

    @Override
    public void drawText(UiDrawer uiDrawer, PtmApplication ptmApplication) {
        myLifeTp.draw(uiDrawer);
        myRepairsExcessTp.draw(uiDrawer);
        myShieldLifeTp.draw(uiDrawer);
        myG1AmmoTp.draw(uiDrawer);
        myG1AmmoExcessTp.draw(uiDrawer);
        myG2AmmoTp.draw(uiDrawer);
        myG2AmmoExcessTp.draw(uiDrawer);
        myChargesExcessTp.draw(uiDrawer);
        myMoneyExcessTp.draw(uiDrawer, TextAlignment.LEFT);

        for (WarnDrawer warnDrawer : warnDrawers) {
            if (warnDrawer.drawPerc > 0) {
                warnDrawer.drawText(uiDrawer);
                break;
            }
        }

        zoneNameAnnouncer.drawText(uiDrawer);
    }

    @Override
    public void blurCustom(PtmApplication ptmApplication) {
        shipControl.blur();
    }

    public boolean isLeft() {
        return shipControl.isLeft();
    }

    public boolean isRight() {
        return shipControl.isRight();
    }

    public boolean isUp() {
        return shipControl.isUp();
    }

    public boolean isDown() {
        return shipControl.isDown();
    }

    public boolean isShoot() {
        return shipControl.isShoot();
    }

    public boolean isShoot2() {
        return shipControl.isShoot2();
    }

    public boolean isAbility() {
        return shipControl.isAbility();
    }

    public static class TextPlace {
        public final Color color;
        public String text;
        public Vector2 pos = new Vector2();

        public TextPlace(Color col) {
            color = new Color(col);
        }

        public void draw(UiDrawer uiDrawer) {
            uiDrawer.drawString(text, pos.x, pos.y, FontSize.HUD, true, color);
        }

        public void draw(UiDrawer uiDrawer, TextAlignment align) {
            uiDrawer.drawString(text, pos.x, pos.y, FontSize.HUD, align, true, color);
        }
    }

    private static class NoShieldWarn extends WarnDrawer {
        public NoShieldWarn(float r) {
            super(r, "No Shield");
        }

        protected boolean shouldWarn(PtmGame game) {
            PtmShip h = game.getHero();
            if (h == null) {
                return false;
            }
            return h.getShield() == null;
        }
    }

    private static class NoArmorWarn extends WarnDrawer {
        public NoArmorWarn(float r) {
            super(r, "No Armor");
        }

        protected boolean shouldWarn(PtmGame game) {
            PtmShip h = game.getHero();
            if (h == null) {
                return false;
            }
            return h.getArmor() == null;
        }
    }

    private static class EnemyWarn extends WarnDrawer {
        public EnemyWarn(float r) {
            super(r, "Dangerous\nEnemy");
        }

        protected boolean shouldWarn(PtmGame game) {
            PtmShip h = game.getHero();
            if (h == null) {
                return false;
            }
            float heroCap = HardnessCalc.getShipDmgCap(h);
            List<PtmObject> objs = game.getObjMan().getObjs();
            FactionManager fm = game.getFactionMan();
            PtmCam cam = game.getCam();
            float viewDist = cam.getViewDist();
            float dps = 0;
            for (int i = 0, sz = objs.size(); i < sz; i++) {
                PtmObject o = objs.get(i);
                if (!(o instanceof PtmShip)) {
                    continue;
                }
                PtmShip ship = (PtmShip) o;
                if (viewDist < ship.getPosition().dst(h.getPosition())) {
                    continue;
                }
                if (!fm.areEnemies(h, ship)) {
                    continue;
                }
                dps += HardnessCalc.getShipDps(ship);
                if (HardnessCalc.isDangerous(heroCap, dps)) {
                    return true;
                }
            }
            return false;
        }
    }
}
