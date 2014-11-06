package org.muscat.kermit.log;

import java.util.Set;

public interface LogEntry {

  public String getChangeID();
  public String getMessage();
  public Set<String> getChangedPaths();
  public int getPathSpaces();

}
