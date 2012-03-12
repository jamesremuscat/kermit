package org.muscat.kermit.log;

import java.util.Collection;

public interface LogListener {

  public void logEntries(final String label, final Collection<LogEntry> entries);

}
