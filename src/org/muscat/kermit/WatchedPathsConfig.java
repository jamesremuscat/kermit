package org.muscat.kermit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.muscat.kermit.log.LogListener;
import org.muscat.kermit.log.LogWatcher;
import org.muscat.kermit.log.svn.SVNLogWatcher;
import org.tmatesoft.svn.core.SVNException;

/**
 * Class that periodically reloads a list of watched SVN paths from a config file and maintains a collection of {@link SVNLogWatcher}s for each path.
 * @author jrem
 */
public class WatchedPathsConfig implements Runnable {

  private static final String LINE_COMMENT_PREFIX = "#";

  private static final char LABEL_SEPARATOR = '!';

  private static final int THREAD_SLEEP_TIME = 60000; // one minute

  private static final String PATHS_FILE_NAME = "config/paths.txt";

  /**
   * The projects we are interested in watching.
   */
  private final Set<WatchedPath> _watchedPaths = new LinkedHashSet<WatchedPath>();

  private final LogListener _listener;

  private final Map<String, LogWatcher> _watchers = new LinkedHashMap<String, LogWatcher>();

  public WatchedPathsConfig(final LogListener listener) {
    _listener = listener;
  }


  public synchronized Set<WatchedPath> getWatchedPaths() {
    return Collections.unmodifiableSet(_watchedPaths);
  }

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {

    final File pathsFile = new File(PATHS_FILE_NAME);

    while (true) {

      if (pathsFile.canRead()) {

        BufferedReader reader = null;

        try {
          reader = new BufferedReader(new FileReader(pathsFile));

          String line = reader.readLine();

          synchronized (this) { // make sure we can't return an empty set by making this synchronized

            _watchedPaths.clear();

            while (line != null) {
              if (!line.startsWith(LINE_COMMENT_PREFIX) && line.length() > 0) {

                final String label;
                final String path;
                final int separator = line.indexOf(LABEL_SEPARATOR);
                if (separator > 0) {
                  label = line.substring(0, separator);
                  path = line.substring(separator + 1);
                }
                else {
                  label = null;
                  path = line;
                }

                _watchedPaths.add(new WatchedPath(label, path));
              }

              line = reader.readLine();
            }

            for (final WatchedPath path : _watchedPaths) {
              if (!_watchers.containsKey(path.getPath())) {
                createNewWatcher(path);
              }
            }

            final Set<String> toRemove = new LinkedHashSet<String>();

            for (final String path : _watchers.keySet()) {
              if (!isWatchingPath(path)) {
                toRemove.add(path);
              }
            }

            for (final String path : toRemove) {
              _watchers.remove(path).stop();
            }

          }
        }
        catch (final FileNotFoundException e) {
          // this really shouldn't happen if canRead() returned true!
        }
        catch (final IOException e) {
          // meh
        }
        finally {
          if (reader != null) {
            try {
              reader.close();
            }
            catch (final IOException e) {
              // Really? I really have to put a frikking try-catch inside a finally to make sure I close this thing?
            }
          }
        }

      }

      try {
        Thread.sleep(THREAD_SLEEP_TIME);
      }
      catch (final InterruptedException e) {
        // move along
      }
    }

  }


  /**
   * @param path
   * @return
   */
  private boolean isWatchingPath(final String path) {
    for (final WatchedPath wp : _watchedPaths) {
      if (wp.getPath().equals(path)) {
        return true;
      }
    }
    return false;
  }


  private void createNewWatcher(final WatchedPath path) {
    try {
      final LogWatcher watcher = new SVNLogWatcher(path);
      new Thread(watcher, "SVN log watcher for " + path).start();
      watcher.addListener(_listener);
      _watchers.put(path.getPath(), watcher);
    }
    catch (final SVNException e) {
      e.printStackTrace();
    }
  }

}
