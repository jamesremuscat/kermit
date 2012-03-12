package org.muscat.kermit;

public class PathWatcherException extends Exception {

  private static final long serialVersionUID = -8207039607627202069L;

  public PathWatcherException(final Throwable t) {
    super(t);
  }

  public PathWatcherException(final String t) {
    super(t);
  }

}
