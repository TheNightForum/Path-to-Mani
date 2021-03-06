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
package com.tnf.ptm.common;

import com.badlogic.gdx.utils.JsonValue;
import com.tnf.ptm.handler.files.HullConfigManager;
import com.tnf.ptm.entities.ship.hulls.HullConfig;
import com.tnf.ptm.entities.item.ItemManager;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;

public class ShipConfig {
    public final HullConfig hull;
    public final String items;
    public final int money;
    public final float density;
    public final ShipConfig guard;
    public final float dps;

    public ShipConfig(HullConfig hull, String items, int money,
                      float density, ShipConfig guard, ItemManager itemManager) {
        this.hull = hull;
        this.items = items;
        this.money = money;
        this.density = density;
        this.guard = guard;
        dps = HardnessCalc.getShipConfDps(this, itemManager);
    }

    public static ArrayList<ShipConfig> loadList(JsonValue shipListJson, HullConfigManager hullConfigs, ItemManager itemManager) {
        ArrayList<ShipConfig> res = new ArrayList<>();
        if (shipListJson == null) {
            return res;
        }
        for (JsonValue shipNode : shipListJson) {
            ShipConfig c = load(hullConfigs, shipNode, itemManager);
            res.add(c);
        }
        return res;
    }

    public static ShipConfig load(HullConfigManager hullConfigs, JsonValue rootNode, ItemManager itemManager) {
        if (rootNode == null) {
            return null;
        }
        String hullName = rootNode.getString("hull");
        HullConfig hull = hullConfigs.getConfig(new ResourceUrn(hullName));
        String items = rootNode.getString("items");
        int money = rootNode.getInt("money", 0);
        float density = rootNode.getFloat("density", -1);
        ShipConfig guard;
        if (rootNode.hasChild("guard")) {
            guard = load(hullConfigs, rootNode.get("guard"), itemManager);
        } else {
            guard = null;
        }
        return new ShipConfig(hull, items, money, density, guard, itemManager);
    }
}
