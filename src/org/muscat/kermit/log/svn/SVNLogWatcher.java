package org.muscat.kermit.log.svn;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.LogEntryImpl;
import org.muscat.kermit.log.LogWatcher;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class SVNLogWatcher extends LogWatcher {

  private static final long SVN_HEAD = -1;
  private long _lastSeenRevision = 0;
  private final SVNRepository _repository;
  private final String _path;

  private final String _label;

  public SVNLogWatcher(final WatchedPath svnPath) throws SVNException {
    super(svnPath);
    final String svnURL = svnPath.getPath();
    _label = svnPath.getLabel();

    DAVRepositoryFactory.setup();
    _repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnURL));
    _lastSeenRevision = _repository.getLatestRevision();

    final SVNURL repositoryRoot = _repository.getRepositoryRoot(true);
    _path = svnURL.replaceAll(repositoryRoot.toString(), "");
  }

  /**
   * @throws SVNException
   */
  @Override
  protected void checkUpdates() {
    try {
      if (_repository.getLatestRevision() > _lastSeenRevision) {
        @SuppressWarnings("unchecked")
        final
        Collection<SVNLogEntry> log = _repository.log(new String[] {_path}, null, _lastSeenRevision + 1, SVN_HEAD, true, false);

        final Collection<LogEntry> converted = convertEntries(log);
        notifyAllListeners(_label, converted);
        for (final SVNLogEntry e : log) {
          if (e.getRevision() > _lastSeenRevision) {
            _lastSeenRevision = e.getRevision();
          }
        }
      }
    }
    catch (final SVNException e) {

    }
  }

  @Override
  protected String getThreadComment() {
    return "SVN log watcher for " + _path;
  }

  private Collection<LogEntry> convertEntries(final Collection<SVNLogEntry> log) {
    final Collection<LogEntry> c = new LinkedHashSet<LogEntry>();

    for (final SVNLogEntry svn : log) {
      @SuppressWarnings("unchecked")
      final LogEntryImpl e = new LogEntryImpl(_label, "r" + Long.toString(svn.getRevision()), svn.getAuthor(), svn.getMessage(), svn.getChangedPaths().keySet());
      c.add(e);
    }

    return c;
  }

  public long getLatestRevision() {
    return _lastSeenRevision;
  }

}
