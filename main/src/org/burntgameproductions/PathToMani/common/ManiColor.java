

package org.burntgameproductions.PathToMani.common;

import com.badlogic.gdx.graphics.Color;

public class ManiColor {
  public static final Color DDG = col(.12f, 1);
  public static final Color DG = col(.25f, 1);
  public static final Color G = col(.5f, 1);
  public static final Color LG = col(.75f, 1);
  public static final Color W50 = col(1, .5f);
  public static final Color W = col(1, 1);

  public static final Color UI_BG = col(0, .9f);
  public static final Color UI_INACTIVE = new Color(0, .75f, 1, .1f);
  public static final Color UI_DARK = new Color(0, .75f, 1, .17f);
  public static final Color UI_MED = new Color(0, .75f, 1, .25f);
  public static final Color UI_LIGHT = new Color(0, .75f, 1, .5f);
  public static final Color UI_OPAQUE = new Color(0, .56f, .75f, 1f);
  public static final Color UI_WARN = new Color(1, .5f, 0, .5f);

  public static final Color Magneta = new Color(.90f, .14f, .30f, 1);
  public static final Color Organge = new Color(.99f, .53f, .11f, 1);
  public static final Color Yellow = new Color(.86f, .86f, .9f, 1);
  public static final Color Cyan = new Color(.16f, .56f, .68f, 1);
  public static final Color Green = new Color(.4f, .88f, .34f, 1);



  public static Color col(float b, float t) {
    return new Color(b, b, b, t);
  }
}
