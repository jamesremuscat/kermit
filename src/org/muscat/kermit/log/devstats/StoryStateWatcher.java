package org.muscat.kermit.log.devstats;

import java.util.Set;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.LogWatcher;

public class StoryStateWatcher extends LogWatcher {

  private StoryStates _lastStates;

  public StoryStateWatcher(final WatchedPath path) {
    super(path);
    _lastStates = StoryStatesParser.parse(path.getPath());
  }

  @Override
  protected String getThreadComment() {
    return "Story state watcher for " + getPath();
  }

  @Override
  protected void checkUpdates() {
    final StoryStates newStates = StoryStatesParser.parse(getPath().getPath());

    final Set<LogEntry> changes = _lastStates.compareTo(newStates);

    notifyAllListeners(null, changes);

    _lastStates = newStates;
  }

}
