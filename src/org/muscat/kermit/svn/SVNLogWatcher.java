package org.muscat.kermit.svn;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.muscat.kermit.WatchedPathsConfig;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SVNLogWatcher implements Runnable {

  private static final long SVN_HEAD = -1;
  private final WatchedPathsConfig _paths;
  private long _lastSeenRevision = 0;
  private final SVNRepository _repository;
  private final Set<SVNLogListener> _listeners = new LinkedHashSet<SVNLogListener>();

  public SVNLogWatcher(final String rootURL, final WatchedPathsConfig paths) throws SVNException {
    _paths = paths;
    DAVRepositoryFactory.setup();
    _repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded(rootURL));
    _lastSeenRevision = _repository.getLatestRevision();
  }

  public void addListener(final SVNLogListener listener) {
    _listeners.add(listener);
  }

  @Override
  public void run() {

    while (true) {
      try {
        if (_repository.getLatestRevision() > _lastSeenRevision) {
          final String[] watchedPaths = _paths.getWatchedPaths().toArray(new String[1]);
          @SuppressWarnings("unchecked")
          Collection<SVNLogEntry> log = _repository.log(watchedPaths, null, _lastSeenRevision + 1, SVN_HEAD, true, false);
          for (SVNLogListener listener : _listeners) {
            listener.logEntries(log);
          }
          for (SVNLogEntry e : log) {
            if (e.getRevision() > _lastSeenRevision) {
              _lastSeenRevision = e.getRevision();
            }
          }
        }
      }
      catch (SVNException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(30000);
      }
      catch (InterruptedException e) {
        // fine, I'll carry on now
      }
    }

  }

  public long getLatestRevision() {
    return _lastSeenRevision;
  }

}
