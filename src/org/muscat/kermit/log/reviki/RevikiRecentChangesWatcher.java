package org.muscat.kermit.log.reviki;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.atom.AtomLogWatcher;

public class RevikiRecentChangesWatcher extends AtomLogWatcher {

  public RevikiRecentChangesWatcher(final WatchedPath logPath) {
    super(logPath);
  }

  @Override
  protected String getRevisionPrefix() {
    return "r";
  }

  @Override
  protected String getThreadComment() {
    return "Reviki log watcher for " + getPath();
  }

  @Override
  protected String getDateFormatString() {
    return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  }

}
