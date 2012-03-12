package org.muscat.kermit.log;

import java.util.Set;

public interface LogEntry {

  public String getAuthor();
  public String getMessage();
  public long getRevision();
  public Set<String> getChangedPaths();

}
