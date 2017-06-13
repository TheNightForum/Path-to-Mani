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
package old.tnf.ptm.game.sound;

import com.tnf.ptm.assets.audio.PlayableSound;
import com.tnf.ptm.assets.audio.OggSound;
import com.tnf.ptm.common.PtmMath;

import java.util.List;

/**
 * Represents a set of random OggSound urns with a single basePitch assigned to every of them.
 * <p>
 * This as an alternative to sounds being randomly fetched from a specified folder -
 * a workflow that isn't viable with gestalt.
 */
public class OggSoundSet implements PlayableSound {
    private final OggSoundManager oggSoundManager;
    private final List<String> urnList;
    private final float basePitch;

    public OggSoundSet(OggSoundManager oggSoundManager, List<String> urnList, float basePitch) {
        this.oggSoundManager = oggSoundManager;
        this.urnList = urnList;
        this.basePitch = basePitch;
    }

    public OggSoundSet(OggSoundManager oggSoundManager, List<String> urnList) {
        this(oggSoundManager, urnList, 1.0f);
    }

    @Override
    public OggSound getOggSound() {
        return oggSoundManager.getSound(PtmMath.elemRnd(urnList));
    }

    @Override
    public float getBasePitch() {
        return basePitch;
    }
}