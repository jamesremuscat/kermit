package org.muscat.kermit;

import junit.framework.TestCase;

public class TestStringUtils extends TestCase {

  public void testSimpleCommonPrefix() {

    assertEquals("", StringUtils.getCommonPrefix(new String[] {}));
    assertEquals("abcdef", StringUtils.getCommonPrefix(new String[] {"abcdef"}));
    assertEquals("abc", StringUtils.getCommonPrefix(new String[] {"abcdef", "abcfed"}));
    assertEquals("abc", StringUtils.getCommonPrefix(new String[] {"abcdef", "abcf"}));
    assertEquals("abc", StringUtils.getCommonPrefix(new String[] {"abc", "abcfart"}));
    assertEquals("ab", StringUtils.getCommonPrefix(new String[] {"abcdef", "abcfed", "ab11"}));
    assertEquals("", StringUtils.getCommonPrefix(new String[] {"abcdef", "ghjik", "lmnop"}));

  }

}
