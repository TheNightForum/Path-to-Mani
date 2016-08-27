

package org.burntgameproductions.PathToMani.game.particle;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.burntgameproductions.PathToMani.common.ManiColor;
import org.burntgameproductions.PathToMani.common.ManiMath;
import org.burntgameproductions.PathToMani.game.dra.Dra;
import org.burntgameproductions.PathToMani.game.dra.DraLevel;
import org.burntgameproductions.PathToMani.game.dra.DrasObject;
import org.burntgameproductions.PathToMani.game.item.Shield;
import org.burntgameproductions.PathToMani.game.ManiGame;
import org.burntgameproductions.PathToMani.game.dra.RectSprite;
import org.burntgameproductions.PathToMani.game.ship.hulls.Hull;

import java.util.ArrayList;

public class PartMan {
  public static final float EXPL_LIGHT_MAX_SZ = .4f;
  public static final float EXPL_LIGHT_MAX_FADE_TIME = .8f;
  public static final float SZ_TO_BLINK_COUNT = 18f;

  public PartMan() {
  }

  public void finish(ManiGame game, ParticleSrc src, Vector2 basePos) {
    if (src.isContinuous()) src.setWorking(false);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(src);
    DrasObject o = new DrasObject(dras, new Vector2(basePos), new Vector2(), null, true, false);
    game.getObjMan().addObjDelayed(o);
  }

  public void blinks(Vector2 pos, ManiGame game, float sz) {
    int count = (int) (SZ_TO_BLINK_COUNT * sz * sz);
    for (int i = 0; i < count; i++) {
      Vector2 lightPos = new Vector2();
      ManiMath.fromAl(lightPos, ManiMath.rnd(180), ManiMath.rnd(0, sz / 2));
      lightPos.add(pos);
      float lightSz = ManiMath.rnd(.5f, 1) * EXPL_LIGHT_MAX_SZ;
      float fadeTime = ManiMath.rnd(.5f, 1) * EXPL_LIGHT_MAX_FADE_TIME;
      LightObject light = new LightObject(game, lightSz, true, 1, lightPos, fadeTime, game.getCols().fire);
      game.getObjMan().addObjDelayed(light);
    }
  }

  public void shieldSpark(ManiGame game, Vector2 collPos, Hull hull, TextureAtlas.AtlasRegion shieldTex, float perc) {
    if (perc <= 0) return;
    Vector2 pos = hull.getPos();
    float angle = ManiMath.angle(pos, collPos);
    float sz = hull.config.getSize() * Shield.SIZE_PERC * 2;
    float alphaSum = perc * 3;
    RectSprite s = null;
    int count = (int) alphaSum + 1;
    for (int i = 0; i < count; i++) {
      s = blip(game, pos, angle, sz, .5f, hull.getSpd(), shieldTex);
    }
    float lastTint = ManiMath.clamp(alphaSum - (int) alphaSum);
    if (s != null) {
      s.tint.a = lastTint;
      s.baseAlpha = lastTint;
    }
  }

  public RectSprite blip(ManiGame game, Vector2 pos, float angle, float sz, float fadeTime, Vector2 spd,
                         TextureAtlas.AtlasRegion tex)
  {
    RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.PART_FG_0, angle, 0, ManiColor.W, true);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    dras.add(s);
    DrasObject o = new DrasObject(dras, new Vector2(pos), new Vector2(spd), null, false, false);
    o.fade(fadeTime);
    game.getObjMan().addObjDelayed(o);
    return s;
  }

}
