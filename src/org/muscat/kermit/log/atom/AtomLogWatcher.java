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

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

  private final Set<String> _seenChanges = new LinkedHashSet<String>();
  private final AtomParser _parser = new AtomParser();

  public AtomLogWatcher(final WatchedPath logPath) {
    super(logPath);
    _parser.setRevisionPrefix(getRevisionPrefix());
    _parser.setDateFormat(getDateFormatString());

    // base set of changes
    try {
      final List<LogEntry> init = _parser.readFromURL(getPath().getPath());
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

  protected String getDateFormatString() {
    return DATE_FORMAT;
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
      final List<LogEntry> changes = _parser.readFromURL(getPath().getPath()); // is immutable

      final Collection<LogEntry> toNotify = new LinkedHashSet<LogEntry>();

      for (final LogEntry change : changes) {
        if (!_seenChanges.contains(change.getRevision())) {
          toNotify.add(change);
        }
      }

      notifyAllListeners(getPath().getLabel(), toNotify);

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

}