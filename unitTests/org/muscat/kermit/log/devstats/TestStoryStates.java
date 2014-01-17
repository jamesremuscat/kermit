package org.muscat.kermit.log.devstats;

import java.util.Set;

import junit.framework.TestCase;

import org.muscat.kermit.log.LogEntry;

public class TestStoryStates extends TestCase {

  public void testNoDifferencesWithSelf() {

    final StoryStates ss = new StoryStates();

    assertTrue(ss.getChangesTo("blort", ss).isEmpty());

    ss.add("Foo", StoryState.AWAITING_STORY_SIGN_OFF);

    assertTrue(ss.getChangesTo("blort", ss).isEmpty());
  }

  public void testStoryAdded() {
    final StoryStates old = new StoryStates();
    final StoryStates noo = new StoryStates();

    old.add("Foo", StoryState.IN_PROGRESS);
    noo.add("Foo", StoryState.IN_PROGRESS);
    noo.add("Bar", StoryState.IN_PROGRESS);

    final Set<LogEntry> changesTo = old.getChangesTo("Baz", noo);

    assertEquals(1, changesTo.size());

  }

}
