package org.muscat.kermit.log.atom;

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

public abstract class AtomLogWatcher extends LogWatcher {

  private final Set<String> _seenChanges = new LinkedHashSet<String>();
  private final AtomParser _parser = AtomParser.Factory.getInstance();
  private final WatchedPath _path;

  public AtomLogWatcher(final WatchedPath logPath) {
    _path = logPath;
    _parser.setRevisionPrefix(getRevisionPrefix());

    // base set of changes
    try {
      final List<LogEntry> init = _parser.readFromURL(_path.getPath());
      _seenChanges.addAll(extractChangesetInfo(init));
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

  protected static final Set<String> extractChangesetInfo(final Collection<LogEntry> logs) {
    final Set<String> changesets = new LinkedHashSet<String>();

    for (final LogEntry e : logs) {
      changesets.add(e.getRevision());
    }

    return Collections.unmodifiableSet(changesets);
  }

  protected String getRevisionPrefix() {
    return "";
  }

  @Override
  protected final void checkUpdates() {

    try {
      final List<LogEntry> changes = _parser.readFromURL(_path.getPath()); // is immutable

      final Collection<LogEntry> toNotify = new LinkedHashSet<LogEntry>();

      for (final LogEntry change : changes) {
        if (!_seenChanges.contains(change.getRevision())) {
          toNotify.add(change);
        }
      }

      notifyAllListeners(_path.getLabel(), toNotify);

      _seenChanges.clear();
      _seenChanges.addAll(extractChangesetInfo(changes));
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


  @Override
  protected String getThreadComment() {
    return "Atom log watcher for " + getPath();
  }

  protected final WatchedPath getPath() {
    return _path;
  }

}