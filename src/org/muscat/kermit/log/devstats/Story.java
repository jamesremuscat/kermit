package org.muscat.kermit.log.devstats;

public class Story {

  private final String _phase;
  private final StoryState _state;

  public Story(final String phase, final StoryState state) {
    _phase = phase;
    _state = state;
  }

  public String getPhase() {
    return _phase;
  }

  public StoryState getState() {
    return _state;
  }

}
