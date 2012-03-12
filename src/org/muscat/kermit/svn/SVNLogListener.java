package org.muscat.kermit.svn;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNLogEntry;

public interface SVNLogListener {

  public void logEntries(final String label, final Collection<SVNLogEntry> entries);

}
