package org.muscat.kermit.log;

import java.util.Set;

public class LogEntryImpl implements LogEntry {

  private final String _author;
  private final String _message;
  private final String _revision;
  private final Set<String> _changedPaths;

  public LogEntryImpl(final String revision, final String author, final String message, final Set<String> changedPaths) {
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

  public String getRevision() {
    return _revision;
  }

  public Set<String> getChangedPaths() {
    return _changedPaths;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_author == null) ? 0 : _author.hashCode());
    result = prime * result + ((_changedPaths == null) ? 0 : _changedPaths.hashCode());
    result = prime * result + ((_message == null) ? 0 : _message.hashCode());
    result = prime * result + ((_revision == null) ? 0 : _revision.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LogEntryImpl)) {
      return false;
    }
    final LogEntryImpl other = (LogEntryImpl) obj;
    if (_author == null) {
      if (other._author != null) {
        return false;
      }
    }
    else if (!_author.equals(other._author)) {
      return false;
    }
    if (_changedPaths == null) {
      if (other._changedPaths != null) {
        return false;
      }
    }
    else if (!_changedPaths.equals(other._changedPaths)) {
      return false;
    }
    if (_message == null) {
      if (other._message != null) {
        return false;
      }
    }
    else if (!_message.equals(other._message)) {
      return false;
    }
    if (_revision == null) {
      if (other._revision != null) {
        return false;
      }
    }
    else if (!_revision.equals(other._revision)) {
      return false;
    }
    return true;
  }

}
