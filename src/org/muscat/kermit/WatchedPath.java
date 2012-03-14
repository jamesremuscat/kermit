package org.muscat.kermit;

import org.muscat.kermit.log.LogWatcher;
import org.muscat.kermit.log.hg.HgLogWatcher;
import org.muscat.kermit.log.reviki.RevikiRecentChangesWatcher;
import org.muscat.kermit.log.svn.SVNLogWatcher;
import org.tmatesoft.svn.core.SVNException;

public class WatchedPath {

  private final String _label;
  private final String _path;
  private final PathType _type;

  public WatchedPath(final PathType type, final String label, final String path) {
    _type = type;
    _label = label;
    _path = path;
  }

  public String getLabel() {
    return _label;
  }

  public String getPath() {
    return _path;
  }

  public PathType getPathType() {
    return _type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_path == null) ? 0 : _path.hashCode());
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
    if (!(obj instanceof WatchedPath)) {
      return false;
    }
    final WatchedPath other = (WatchedPath) obj;
    if (_path == null) {
      if (other._path != null) {
        return false;
      }
    }
    else if (!_path.equals(other._path)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return _label + "!" + _type + "!" + _path;
  }

  public LogWatcher createWatcher() throws PathWatcherException {
    return _type.getWatcher(this);
  }

  public enum PathType {
    SVN {
      @Override
      public LogWatcher getWatcher(final WatchedPath path) throws PathWatcherException {
        try {
          return new SVNLogWatcher(path);
        }
        catch (final SVNException e) {
          throw new PathWatcherException(e);
        }
      }
    },
    HG {
      @Override
      public LogWatcher getWatcher(final WatchedPath path) throws PathWatcherException {
        return new HgLogWatcher(path);
      }
    },
    REVIKI {
      @Override
      public LogWatcher getWatcher(final WatchedPath path) throws PathWatcherException {
        return new RevikiRecentChangesWatcher(path);
      }
    }
    ;

    public abstract LogWatcher getWatcher(final WatchedPath path) throws PathWatcherException;

    public static PathType fromString(final String s) throws PathWatcherException {
      if ("svn".equalsIgnoreCase(s)) {
        return SVN;
      }
      if ("hg".equalsIgnoreCase(s)) {
        return HG;
      }
      if ("reviki".equalsIgnoreCase(s)) {
        return REVIKI;
      }
      throw new PathWatcherException("Unknown path type: " + s);
    }
  }

}
