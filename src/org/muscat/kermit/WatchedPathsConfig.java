package org.muscat.kermit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that periodically reloads a list of watched SVN paths from a config file.
 * @author jrem
 */
public class WatchedPathsConfig implements Runnable {

  private static final int THREAD_SLEEP_TIME = 60000; // one minute

  private static final String PATHS_FILE_NAME = "config/paths.txt";

  /**
   * The projects we are interested in watching.
   */
  private final Set<String> _watchedPaths = new LinkedHashSet<String>();

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
          }
        }
        catch (FileNotFoundException e) {
          // this really shouldn't happen if canRead() returned true!
        }
        catch (IOException e) {
          // meh
        }
        finally {
          if (reader != null) {
            try {
              reader.close();
            }
            catch (IOException e) {
              // Really? I really have to put a frikking try-catch inside a finally to make sure I close this thing?
            }
          }
        }

      }

      try {
        Thread.sleep(THREAD_SLEEP_TIME);
      }
      catch (InterruptedException e) {
        // move along
      }
    }

  }

}
