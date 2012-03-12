package org.muscat.kermit.log;

import java.util.Set;

public class LogEntryImpl implements LogEntry {

  private final String _author;
  private final String _message;
  private final long _revision;
  private final Set<String> _changedPaths;

  public LogEntryImpl(final long revision, final String author, final String message, final Set<String> changedPaths) {
    _author = author;
    _message = message;
    _revision = revision;
    _changedPaths = changedPaths;
  }

  public String getAuthor() {
    return _author;
  }

  public String getMessage() {
    return _message;
  }

  public long getRevision() {
    return _revision;
  }

  public Set<String> getChangedPaths() {
    return _changedPaths;
  }

}
