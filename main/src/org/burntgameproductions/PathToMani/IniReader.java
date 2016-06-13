

package org.burntgameproductions.PathToMani;

import com.badlogic.gdx.files.FileHandle;
import org.burntgameproductions.PathToMani.files.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IniReader {

  private final HashMap<String,String> myVals;

  public IniReader(String fileName, ManiFileReader reader, boolean readOnly) {
    myVals = new HashMap<String, String>();
    List<String> lines = reader != null ? reader.read(fileName) : fileToLines(fileName, readOnly);

    for (String line : lines) {
      int commentStart = line.indexOf('#');
      if (commentStart >= 0) {
        line = line.substring(0, commentStart);
      }
      String[] sides = line.split("=");
      if (sides.length < 2) continue;
      String key = sides[0].trim();
      String val = sides[1].trim();
      myVals.put(key, val);
    }
  }

  private List<String> fileToLines(String fileName, boolean readOnly) {
    FileManager.FileLocation accessType = readOnly ? FileManager.FileLocation.STATIC_FILES : FileManager.FileLocation.DYNAMIC_FILES;
    FileHandle fh = FileManager.getInstance().getFile(fileName, accessType);

    ArrayList<String> res = new ArrayList<String>();
    if (!fh.exists()) return res;
    for (String s : fh.readString().split("\n")) {
      res.add(s);
    }
    return res;
  }

  public String getString(String key, String defaultValue) {
    String st = myVals.get(key);
    return st == null ? defaultValue : st;
  }

  public int getInt(String key, int defaultValue) {
    String st = myVals.get(key);
    return st == null ? defaultValue : Integer.parseInt(st);
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    String st = myVals.get(key);
    return st == null ? defaultValue : "true".equalsIgnoreCase(st);
  }

  public float getFloat(String key, float defaultValue) {
    String st = myVals.get(key);
    return st == null ? defaultValue : Float.parseFloat(st);
  }

  public static void write(String fileName, Object ... keysVals) {
    boolean second = false;
    StringBuilder sb = new StringBuilder();
    for (Object o : keysVals) {
      String s = o.toString();
      sb.append(s);
      sb.append(second ? '\n' : '=');
      second = !second;
    }
    FileHandle file = FileManager.getInstance().getDynamicFile(fileName);
    file.writeString(sb.toString(), false);
  }

}