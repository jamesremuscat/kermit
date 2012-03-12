package org.muscat.kermit.log;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class LogWatcher implements Runnable {

  private final Set<LogListener> _listeners = new LinkedHashSet<LogListener>();
  private boolean _keepRunning;

  public LogWatcher() {
    _keepRunning = true;
  }

  /**
   * Create a new instance of a {@link Thread} and start it running this {@link LogWatcher}.
   * @return the {@link Thread} created
   */
  public final Thread start() {
    final Thread t = new Thread(this, getThreadComment());
    t.start();
    return t;
  }

  /**
   * Implementations may override this method to provide a more specific name for threads created from their implementation.
   * @return
   */
  protected String getThreadComment() {
    return "Generic watcher";
  }

  public final void stop() {
    _keepRunning = false;
  }

  public final void addListener(final LogListener listener) {
    _listeners.add(listener);
  }

  protected final Set<LogListener> getListeners() {
    return _listeners;
  }

  @Override
  public final void run() {

    while (_keepRunning) {
      checkUpdates();

      try {
        Thread.sleep(30000);
      }
      catch (final InterruptedException e) {
        // fine, I'll carry on now
      }
    }

  }

  /**
   * Implementations should check for updates and notify listeners in this method.
   */
  protected abstract void checkUpdates();

}