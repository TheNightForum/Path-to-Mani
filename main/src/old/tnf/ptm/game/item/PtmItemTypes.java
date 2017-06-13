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
package old.tnf.ptm.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;
import old.tnf.ptm.assets.audio.OggSound;
import old.tnf.ptm.game.GameColors;
import old.tnf.ptm.game.sound.OggSoundManager;
import old.tnf.ptm.assets.Assets;
import old.tnf.ptm.assets.json.Json;
import org.terasology.assets.ResourceUrn;

public class PtmItemTypes {
    public final PtmItemType clip;
    public final PtmItemType shield;
    public final PtmItemType armor;
    public final PtmItemType abilityCharge;
    public final PtmItemType gun;
    public final PtmItemType money;
    public final PtmItemType medMoney;
    public final PtmItemType bigMoney;
    public final PtmItemType repair;
    public final PtmItemType fixedGun;

    public PtmItemTypes(OggSoundManager soundManager, GameColors cols) {
        Json json = Assets.getJson(new ResourceUrn("core:types"));
        JsonValue rootNode = json.getJsonValue();

        clip = load(rootNode.get("clip"), soundManager, cols);
        shield = load(rootNode.get("shield"), soundManager, cols);
        armor = load(rootNode.get("armor"), soundManager, cols);
        abilityCharge = load(rootNode.get("abilityCharge"), soundManager, cols);
        gun = load(rootNode.get("gun"), soundManager, cols);
        fixedGun = load(rootNode.get("fixedGun"), soundManager, cols);
        money = load(rootNode.get("money"), soundManager, cols);
        medMoney = load(rootNode.get("medMoney"), soundManager, cols);
        bigMoney = load(rootNode.get("bigMoney"), soundManager, cols);
        repair = load(rootNode.get("repair"), soundManager, cols);

        json.dispose();
    }

    private PtmItemType load(JsonValue itemNode, OggSoundManager soundManager, GameColors cols) {
        Color color = cols.load(itemNode.getString("color"));
        OggSound pickUpSound = soundManager.getSound(itemNode.getString("pickUpSound"));
        float sz = itemNode.getFloat("sz");
        return new PtmItemType(color, pickUpSound, sz);
    }
}
