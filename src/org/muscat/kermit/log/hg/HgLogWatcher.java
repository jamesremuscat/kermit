package org.muscat.kermit.log.hg;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.LogWatcher;
import org.xml.sax.SAXException;

public class HgLogWatcher extends LogWatcher {

  private final Set<String> _seenChangesets = new LinkedHashSet<String>();
  private final AtomParser _parser = AtomParser.Factory.getInstance();
  private final WatchedPath _path;

  public HgLogWatcher(final WatchedPath logPath) {
    _path = logPath;

    // base set of changes
    try {
      final List<LogEntry> init = _parser.readFromURL(_path.getPath());
      _seenChangesets.addAll(extractChangesetInfo(init));
    }
    catch (final SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static final Set<String> extractChangesetInfo(final Collection<LogEntry> logs) {
    final Set<String> changesets = new LinkedHashSet<String>();

    for (final LogEntry e : logs) {
      changesets.add(e.getRevision());
    }

    return Collections.unmodifiableSet(changesets);
  }

  @Override
  protected void checkUpdates() {

    try {
      final List<LogEntry> changes = _parser.readFromURL(_path.getPath()); // is immutable

      final Collection<LogEntry> toNotify = new LinkedHashSet<LogEntry>();

      for (final LogEntry change : changes) {
        if (!_seenChangesets.contains(change.getRevision())) {
          toNotify.add(change);
        }
      }

      notifyAllListeners(_path.getLabel(), toNotify);

      _seenChangesets.clear();
      _seenChangesets.addAll(extractChangesetInfo(changes));
    }
    catch (final SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
