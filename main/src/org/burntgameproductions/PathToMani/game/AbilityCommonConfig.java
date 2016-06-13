

package org.burntgameproductions.PathToMani.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import org.burntgameproductions.PathToMani.TextureManager;
import org.burntgameproductions.PathToMani.game.particle.EffectConfig;
import org.burntgameproductions.PathToMani.game.particle.EffectTypes;
import org.burntgameproductions.PathToMani.game.sound.SoundManager;
import org.burntgameproductions.PathToMani.game.sound.ManiSound;

public class AbilityCommonConfig {
  public final EffectConfig effect;
  public final ManiSound activatedSound;

  public AbilityCommonConfig(EffectConfig effect, ManiSound activatedSound) {
    this.effect = effect;
    this.activatedSound = activatedSound;
  }

  public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TextureManager textureManager, GameColors cols,
                                         FileHandle configFile, SoundManager soundManager)
  {
    EffectConfig ec = EffectConfig.load(node.get("effect"), types, textureManager, configFile, cols);
    ManiSound activatedSound = soundManager.getSound(node.getString("activatedSound"), configFile);
    return new AbilityCommonConfig(ec, activatedSound);
  }
}
