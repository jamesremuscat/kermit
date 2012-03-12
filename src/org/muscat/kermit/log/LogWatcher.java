package org.muscat.kermit.log;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class LogWatcher implements Runnable {

  protected final Set<LogListener> _listeners = new LinkedHashSet<LogListener>();
  private boolean _keepRunning;

  public LogWatcher() {
    _keepRunning = true;
  }

  public void stop() {
    _keepRunning = false;
  }

  public void addListener(final LogListener listener) {
    _listeners.add(listener);
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

  protected abstract void checkUpdates();

}