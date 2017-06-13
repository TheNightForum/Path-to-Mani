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
package old.tnf.ptm.game.planet;

import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.gfx.TextureManager;
import com.tnf.ptm.common.PtmMath;
import old.tnf.ptm.files.HullConfigManager;
import old.tnf.ptm.game.ShipConfig;
import old.tnf.ptm.game.chunk.SpaceEnvConfig;
import old.tnf.ptm.game.item.ItemManager;
import old.tnf.ptm.game.item.TradeConfig;
import com.tnf.ptm.assets.Assets;
import com.tnf.ptm.assets.json.Json;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SysConfigs {
    private final Map<String, SysConfig> myConfigs;
    private final Map<String, SysConfig> myHardConfigs;
    private final Map<String, SysConfig> myBeltConfigs;
    private final Map<String, SysConfig> myHardBeltConfigs;

    public SysConfigs(TextureManager textureManager, HullConfigManager hullConfigs, ItemManager itemManager) {
        myConfigs = new HashMap<>();
        myHardConfigs = new HashMap<>();
        myBeltConfigs = new HashMap<>();
        myHardBeltConfigs = new HashMap<>();

        load(textureManager, hullConfigs, false, new ResourceUrn("core:systemsConfig"), itemManager);
        load(textureManager, hullConfigs, true, new ResourceUrn("core:asteroidBeltsConfig"), itemManager);
    }

    private void load(TextureManager textureManager, HullConfigManager hullConfigs, boolean belts,
                          ResourceUrn configName, ItemManager itemManager) {
        Json json = Assets.getJson(configName);
        JsonValue rootNode = json.getJsonValue();

        for (JsonValue sh : rootNode) {
            ArrayList<ShipConfig> tempEnemies = ShipConfig.loadList(sh.get("temporaryEnemies"), hullConfigs, itemManager);
            ArrayList<ShipConfig> innerTempEnemies = ShipConfig.loadList(sh.get("innerTemporaryEnemies"), hullConfigs, itemManager);
            SpaceEnvConfig envConfig = new SpaceEnvConfig(sh.get("environment"), textureManager);

            ArrayList<ShipConfig> constEnemies = null;
            ArrayList<ShipConfig> constAllies = null;
            if (!belts) {
                constEnemies = ShipConfig.loadList(sh.get("constantEnemies"), hullConfigs, itemManager);
                constAllies = ShipConfig.loadList(sh.get("constantAllies"), hullConfigs, itemManager);
            }
            TradeConfig tradeConfig = TradeConfig.load(itemManager, sh.get("trading"), hullConfigs);
            boolean hard = sh.getBoolean("hard", false);
            SysConfig c = new SysConfig(sh.name, tempEnemies, envConfig, constEnemies, constAllies, tradeConfig, innerTempEnemies, hard);
            Map<String, SysConfig> configs = belts ? hard ? myHardBeltConfigs : myBeltConfigs : hard ? myHardConfigs : myConfigs;
            configs.put(sh.name, c);
        }
    }

    public SysConfig getRandomBelt(boolean hard) {
        Map<String, SysConfig> config = hard ? myHardBeltConfigs : myBeltConfigs;
        return PtmMath.elemRnd(new ArrayList<SysConfig>(config.values()));
    }

    public SysConfig getConfig(String name) {
        SysConfig res = myConfigs.get(name);
        if (res != null) {
            return res;
        }
        return myHardConfigs.get(name);
    }

    public SysConfig getRandomCfg(boolean hard) {
        Map<String, SysConfig> config = hard ? myHardConfigs : myConfigs;
        return PtmMath.elemRnd(new ArrayList<SysConfig>(config.values()));
    }

    public void addAllConfigs(ArrayList<ShipConfig> l) {
        for (SysConfig sc : myConfigs.values()) {
            l.addAll(sc.constAllies);
            l.addAll(sc.constEnemies);
            l.addAll(sc.tempEnemies);
            l.addAll(sc.innerTempEnemies);
        }
        for (SysConfig sc : myHardConfigs.values()) {
            l.addAll(sc.constAllies);
            l.addAll(sc.constEnemies);
            l.addAll(sc.tempEnemies);
            l.addAll(sc.innerTempEnemies);
        }
        for (SysConfig sc : myBeltConfigs.values()) {
            l.addAll(sc.tempEnemies);
        }
        for (SysConfig sc : myHardBeltConfigs.values()) {
            l.addAll(sc.tempEnemies);
        }
    }
}
