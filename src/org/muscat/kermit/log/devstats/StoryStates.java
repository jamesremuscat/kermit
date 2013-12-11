package org.muscat.kermit.log.devstats;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

  public Set<LogEntry> compareTo(final StoryStates newStates) {
    final Set<LogEntry> entries = new LinkedHashSet<LogEntry>();

    return entries;
  }

}
