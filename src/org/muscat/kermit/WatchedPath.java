package org.muscat.kermit;

public class WatchedPath {

  final String _label;
  final String _path;

  public WatchedPath(final String label, final String path) {
    _label = label;
    _path = path;
  }

  public String getLabel() {
    return _label;
  }

  public String getPath() {
    return _path;
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



}
