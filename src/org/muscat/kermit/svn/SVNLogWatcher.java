package org.muscat.kermit.svn;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.muscat.kermit.WatchedPath;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SVNLogWatcher implements Runnable {

  private static final long SVN_HEAD = -1;
  private long _lastSeenRevision = 0;
  private final SVNRepository _repository;
  private final String _path;
  private final Set<SVNLogListener> _listeners = new LinkedHashSet<SVNLogListener>();
  private boolean _keepRunning;
  private final String _label;

  public SVNLogWatcher(final WatchedPath svnPath) throws SVNException {

    final String svnURL = svnPath.getPath();
    _label = svnPath.getLabel();

    DAVRepositoryFactory.setup();
    _repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnURL));
    _lastSeenRevision = _repository.getLatestRevision();

    final SVNURL repositoryRoot = _repository.getRepositoryRoot(true);
    _path = svnURL.replaceAll(repositoryRoot.toString(), "");

    System.out.println("Listening to path " + _path + " on repository " + _repository.getRepositoryRoot(false));

    _keepRunning = true;
  }

  public void addListener(final SVNLogListener listener) {
    _listeners.add(listener);
  }

  public void stop() {
    _keepRunning = false;
  }

  @Override
  public void run() {

    while (_keepRunning) {
      try {
        if (_repository.getLatestRevision() > _lastSeenRevision) {
          @SuppressWarnings("unchecked")
          final
          Collection<SVNLogEntry> log = _repository.log(new String[] {_path}, null, _lastSeenRevision + 1, SVN_HEAD, _keepRunning, false);
          for (final SVNLogListener listener : _listeners) {
            listener.logEntries(_label, log);
          }
          for (final SVNLogEntry e : log) {
            if (e.getRevision() > _lastSeenRevision) {
              _lastSeenRevision = e.getRevision();
            }
          }
        }
      }
      catch (final SVNException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(30000);
      }
      catch (final InterruptedException e) {
        // fine, I'll carry on now
      }
    }

  }

  public long getLatestRevision() {
    return _lastSeenRevision;
  }

}
