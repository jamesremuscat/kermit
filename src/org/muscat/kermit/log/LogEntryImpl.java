package org.muscat.kermit.log;

import java.util.Set;

import org.jibble.pircbot.Colors;

public class LogEntryImpl implements LogEntry {

  private final String _author;
  private final String _message;
  private final String _revision;
  private final Set<String> _changedPaths;
  private final String _label;

  public LogEntryImpl(final String label, final String revision, final String author, final String logMessage, final Set<String> changedPaths) {
    _label = label;
    _author = author;
    _message = logMessage;
    _revision = revision;
    _changedPaths = changedPaths;
  }

  public String getAuthor() {
    return _author;
  }

  public String getMessage() {
    return Colors.GREEN + getChangeID() + Colors.NORMAL + " in " + Colors.YELLOW + _label +  Colors.NORMAL + " by " + Colors.BOLD + getAuthor() + Colors.NORMAL + ": " + _message.split("\n")[0];
  }

  protected String getOriginalMessage() {
    return _message;
  }

  public String getChangeID() {
    return _revision;
  }

  public Set<String> getChangedPaths() {
    return _changedPaths;
  }

  @Override
  public int getPathSpaces() {
    return (int) Math.round(Math.floor(getChangeID().length()));
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
