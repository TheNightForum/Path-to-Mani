

package org.burntgameproductions.PathToMani;

import java.nio.file.Path;
import java.util.List;

public interface ManiFileReader {
    Path create(String fileName, List<String> lines);

    List<String> read(String fileName);
}
