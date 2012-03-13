package org.muscat.kermit.log.hg;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.atom.AtomLogWatcher;

public class HgLogWatcher extends AtomLogWatcher {

  public HgLogWatcher(final WatchedPath logPath) {
    super(logPath);
  }

  @Override
  protected String getThreadComment() {
    return "Hg log watcher for " + getPath();
  }

}
