package org.muscat.kermit.log.quips;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jibble.pircbot.Colors;
import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.atom.AtomLogEntry;
import org.muscat.kermit.log.atom.AtomLogWatcher;
import org.muscat.kermit.log.atom.AtomParser;
import org.xml.sax.SAXException;

public class QuipsAtomFeedWatcher extends AtomLogWatcher {

  public QuipsAtomFeedWatcher(final WatchedPath logPath) {
    super(new QuipsAtomParser(), logPath);
  }

  @Override
  protected String getThreadComment() {
    return "Quips feed watcher for " + getPath();
  }

  @Override
  protected String getRevisionPrefix() {
    return "Quip ";
  }

  private static class QuipsAtomParser extends AtomParser {

    @Override
    public List<LogEntry> readFromURL(final String url) throws SAXException, IOException {
      final List<LogEntry> result = new ArrayList<LogEntry>();

      for (final LogEntry e: super.readFromURL(url)) {
        result.add(new Quip((AtomLogEntry) e));
      }

      return result;

    }

  }

  private static class Quip implements LogEntry {

    private final AtomLogEntry _original;

    public Quip(final AtomLogEntry original) {
      _original = original;
    }

    @Override
    public String getChangeID() {
      return _original.getChangeID();
    }

    @Override
    public String getMessage() {
      return Colors.GREEN + getChangeID() + Colors.NORMAL + " in " + Colors.YELLOW + "Quips List" + Colors.NORMAL + ": " + _original.getRawMessage().split("\n")[0];
    }

    @Override
    public Set<String> getChangedPaths() {
      return Collections.emptySet();
    }

    @Override
    public int getPathSpaces() {
      return 0;
    }

  }

}
