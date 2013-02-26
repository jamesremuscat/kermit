package org.muscat.kermit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathUtils {

  private PathUtils() {
    // no construction for you
  }

  public static String extractLongestCommonParentPath(final String[] paths) {

    final List<String[]> quantisedPaths = new ArrayList<String[]>();

    for (final String p : paths) {
      quantisedPaths.add(p.split("/"));
    }

    final String[] candidate = quantisedPaths.get(0);
    int maxMatchLength = candidate.length;

    for (int i = 1; i < quantisedPaths.size(); i++) {
      int pathIndex = 0;
      final String[] currentPath = quantisedPaths.get(i);

      while (candidate[pathIndex].equals(currentPath[pathIndex])) {
        pathIndex++;
      }

      maxMatchLength = Math.min(maxMatchLength, pathIndex);

    }

    final String[] match = Arrays.copyOfRange(candidate, 0, maxMatchLength);

    final StringBuilder b = new StringBuilder();

    for (final String s : match) {
      b.append('/');
      b.append(s);
    }

    return b.toString().replaceFirst("\\/", "");
  }

}
