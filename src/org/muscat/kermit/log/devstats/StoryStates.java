package org.muscat.kermit.log.devstats;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jibble.pircbot.Colors;
import org.muscat.kermit.log.LogEntry;

public class StoryStates {

  private final Map<String, StoryState> _states = new LinkedHashMap<String, StoryState>();

  public void add(final String story, final StoryState state) {
    _states.put(story, state);
  }

  public boolean hasStory(final String story) {
    return _states.containsKey(story);
  }

  public StoryState getState(final String story) {
    return _states.get(story);
  }

  public Set<LogEntry> getChangesTo(final String label, final StoryStates newStates) {
    final Set<LogEntry> entries = new LinkedHashSet<LogEntry>();

    for (final String story : _states.keySet()) {
      if (newStates._states.containsKey(story)) {
        final StoryState oldState = getState(story);
        final StoryState newState = newStates.getState(story);
        if (oldState != newState) {
          entries.add(new StoryStateChangedEntry(label, oldState, newState, story));
        }
      }
      else {
        // story has been deleted
        entries.add(new LogEntry() {
          @Override public String getMessage() { return Colors.GREEN + story + Colors.NORMAL + " in " + Colors.YELLOW + label + Colors.NORMAL + " has been " + Colors.RED + "deleted" + Colors.NORMAL; }
          @Override public Set<String> getChangedPaths() { return Collections.singleton(story);  }
          @Override public String getChangeID() { return story; }
        });
      }

      for (final String newStory : newStates._states.keySet()) {

        if (!_states.containsKey(newStory)) {
          // story has been added
          entries.add(new LogEntry() {
            @Override public String getMessage() { return Colors.GREEN + story + Colors.NORMAL + " in " + Colors.YELLOW + label + Colors.NORMAL + " has been " + Colors.GREEN + "created" + Colors.NORMAL + " (" + newStates.getState(story) + ")"; }
            @Override public Set<String> getChangedPaths() { return Collections.singleton(story);  }
            @Override public String getChangeID() { return story; }
          });
        }

      }

    }

    return entries;
  }


  private final class StoryStateChangedEntry implements LogEntry {
    private final StoryState _oldState;

    private final StoryState _newState;

    private final String _story;

    private final String _label;

    private StoryStateChangedEntry(final String label, final StoryState oldState, final StoryState newState, final String story) {
      _label = label;
      _oldState = oldState;
      _newState = newState;
      _story = story;
    }

    @Override
    public String getMessage() {
      return Colors.GREEN + _story + Colors.NORMAL + " in " + Colors.YELLOW + _label + Colors.NORMAL + " changed from " + _oldState.toString() + " to " + Colors.BOLD + _newState.toString() + Colors.NORMAL;
    }

    @Override
    public Set<String> getChangedPaths() {
      return Collections.singleton(_story);
    }

    @Override
    public String getChangeID() {
      return _story;
    }
  }


}
