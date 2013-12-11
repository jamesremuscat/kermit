package org.muscat.kermit.log.devstats;

import java.util.NoSuchElementException;

public enum StoryState {

  BACKLOG("Backlog"),
  LATER_STORY("LaterStory"),
  ESTIMATE_REQUIRED("EstimateRequired"),
  IMPLEMENTATION_REQUIRED("ImplementationRequired"),
  IN_PROGRESS("InProgress"),
  FEEDBACK_REQUIRED("FeedbackRequired"),
  AWAITING_STORY_SIGN_OFF("AwaitingStorySignOff"),
  SIGNED_OFF_STORY("SignedOffStory"),
  IN_VERIFICATION("InVerification"),
  VERIFIED_STORY("VerifiedStory"),
  CLOSED_STORY("ClosedStory");

  private final String _stringForm;

  private StoryState(final String stringForm) {
    _stringForm = stringForm;
  }

  @Override
  public String toString() {
    return _stringForm;
  }

  public static StoryState fromStringForm(final String form) {
    for (final StoryState s : values()) {
      if (s._stringForm.equals(form)) {
        return s;
      }
    }
    throw new NoSuchElementException(form);
  }

}
