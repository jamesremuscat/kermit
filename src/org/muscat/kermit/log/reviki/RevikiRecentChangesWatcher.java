package org.muscat.kermit.log.reviki;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.hg.HgLogWatcher;

public class RevikiRecentChangesWatcher extends HgLogWatcher {

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

}
