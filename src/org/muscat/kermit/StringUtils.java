package org.muscat.kermit;


public class StringUtils {

  public static String getCommonPrefix(final String[] strs) {

    if (strs.length == 0) {
      return "";
    }
    else if (strs.length == 1) {
      return strs[0];
    }
    else {

      int maxCommon = 0;
      while(maxCommon < strs[0].length() && maxCommon < strs[1].length() && strs[0].charAt(maxCommon) == strs[1].charAt(maxCommon)) {
        maxCommon++;
      }

      for (int i = 2; i < strs.length; i++) {
        while(maxCommon > 0 && strs[0].charAt(maxCommon - 1) != strs[i].charAt(maxCommon - 1)) {
          maxCommon--;
        }
      }

      return strs[0].substring(0, maxCommon);

    }

  }

  public static String spaces(final int count) {
    StringBuilder sb = new StringBuilder(count);

    for (int i = 0; i < count; i++) {
      sb.append(" ");
    }


    return sb.toString();
  }

}
