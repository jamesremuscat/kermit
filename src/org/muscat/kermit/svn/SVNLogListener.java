package org.muscat.kermit.svn;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNLogEntry;

public interface SVNLogListener {

  public void logEntries(final Collection<SVNLogEntry> entries);

}
