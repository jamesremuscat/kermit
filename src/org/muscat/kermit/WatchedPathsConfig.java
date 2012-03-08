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

import org.muscat.kermit.svn.SVNLogListener;
import org.muscat.kermit.svn.SVNLogWatcher;
import org.tmatesoft.svn.core.SVNException;

/**
 * Class that periodically reloads a list of watched SVN paths from a config file and maintains a collection of {@link SVNLogWatcher}s for each path.
 * @author jrem
 */
public class WatchedPathsConfig implements Runnable {

  private static final int THREAD_SLEEP_TIME = 60000; // one minute

  private static final String PATHS_FILE_NAME = "config/paths.txt";

  /**
   * The projects we are interested in watching.
   */
  private final Set<String> _watchedPaths = new LinkedHashSet<String>();

  private final SVNLogListener _listener;

  private final Map<String, SVNLogWatcher> _watchers = new LinkedHashMap<String, SVNLogWatcher>();

  public WatchedPathsConfig(final SVNLogListener listener) {
    _listener = listener;
  }


  public synchronized Set<String> getWatchedPaths() {
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
              if (!line.startsWith("#") && line.length() > 0) {
                _watchedPaths.add(line);
              }

              line = reader.readLine();
            }

            for (final String path : _watchedPaths) {
              if (!_watchers.containsKey(path)) {
                createNewWatcher(path);
              }
            }

            for (final String path : _watchers.keySet()) {
              if (!_watchedPaths.contains(path)) {
                _watchers.remove(path).stop();
              }
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


  private void createNewWatcher(final String path) {
    try {
      final SVNLogWatcher watcher = new SVNLogWatcher(path);
      new Thread(watcher, "SVN log watcher for " + path).start();
      watcher.addListener(_listener);
      _watchers.put(path, watcher);
    }
    catch (final SVNException e) {
      e.printStackTrace();
    }
  }

}
