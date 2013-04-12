package org.muscat.kermit.log.quips;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.atom.AtomLogWatcher;

public class QuipsAtomFeedWatcher extends AtomLogWatcher {

  public QuipsAtomFeedWatcher(final WatchedPath logPath) {
    super(logPath);
  }

  @Override
  protected String getThreadComment() {
    return "Quips feed watcher for " + getPath();
  }

  @Override
  protected String getRevisionPrefix() {
    return "Quip ";
  }

}
