package org.muscat.kermit.log.hg;

import java.util.Date;
import java.util.Set;

import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.LogEntryImpl;

public class AtomLogEntry extends LogEntryImpl implements LogEntry {

  private final Date _date;

  public AtomLogEntry(final String revision, final Date date, final String author, final String message, final Set<String> changedPaths) {
    super(revision, author, message, changedPaths);
    _date = date;
  }

  public Date getDate() {
    return _date;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((_date == null) ? 0 : _date.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof AtomLogEntry)) {
      return false;
    }
    final AtomLogEntry other = (AtomLogEntry) obj;
    if (_date == null) {
      if (other._date != null) {
        return false;
      }
    }
    else if (!_date.equals(other._date)) {
      return false;
    }

    return (super.equals(obj));
  }

}
