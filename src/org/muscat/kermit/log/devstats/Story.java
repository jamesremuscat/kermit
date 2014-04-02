package org.muscat.kermit.log.devstats;

public class Story {

  private final String _phase;
  private final StoryState _state;
  private final String _priority;

  public Story(final String phase, final StoryState state, final String priority) {
    _phase = phase;
    _state = state;
    _priority = priority;
  }

  public String getPhase() {
    return _phase;
  }

  public StoryState getState() {
    return _state;
  }

  public String getPriority() {
    return _priority;
  }

}
