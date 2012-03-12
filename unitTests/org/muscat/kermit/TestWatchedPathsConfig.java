package org.muscat.kermit;

import junit.framework.TestCase;

public class TestWatchedPathsConfig extends TestCase {

  public void testExtractLabels() {
    assertEquals("Foo", WatchedPathsConfig.extractLabel("Foo!svn!http://something/"));
    assertNull(WatchedPathsConfig.extractLabel("svn!http://something/"));
  }

  public void testExtractTypes() {
    assertEquals("svn", WatchedPathsConfig.extractType("Foo!svn!http://something/"));
    assertEquals("fom", WatchedPathsConfig.extractType("fom!http://something/"));
  }

  public void testExtractPaths() {
    assertEquals("http://something/", WatchedPathsConfig.extractPath("Foo!svn!http://something/"));
    assertEquals("http://something-else.com/", WatchedPathsConfig.extractPath("fom!http://something-else.com/"));
  }

}
