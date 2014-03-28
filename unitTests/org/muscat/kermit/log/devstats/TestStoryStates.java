package org.muscat.kermit.log.devstats;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.muscat.kermit.log.LogEntry;

public class TestStoryStates extends TestCase {

  public void testNoDifferencesWithSelf() {

    final StoryStates ss = new StoryStates();

    assertTrue(ss.getChangesTo("blort", ss).isEmpty());

    ss.add("Foo", "--", StoryState.AWAITING_STORY_SIGN_OFF);

    assertTrue(ss.getChangesTo("blort", ss).isEmpty());
  }

  public void testStoryAdded() {
    final StoryStates old = new StoryStates();
    final StoryStates noo = new StoryStates();

    old.add("Foo", "--", StoryState.IN_PROGRESS);
    noo.add("Foo", "--", StoryState.IN_PROGRESS);
    noo.add("Bar", "--", StoryState.IN_PROGRESS);

    final Set<LogEntry> changesTo = old.getChangesTo("Baz", noo);

    assertEquals(1, changesTo.size());
    assertEquals("09Bar in 08Baz has been 09created (InProgress)", changesTo.iterator().next().getMessage());

  }

  public void testStoryMoved() {
    final StoryStates old = new StoryStates();
    final StoryStates noo = new StoryStates();

    old.add("Foo", "Iteration1", StoryState.IMPLEMENTATION_REQUIRED);
    noo.add("Foo", "Iteration2", StoryState.IMPLEMENTATION_REQUIRED);

    final Set<LogEntry> changesTo = old.getChangesTo("Baz", noo);

    assertEquals(1, changesTo.size());
    assertEquals("09Foo in 08Baz has moved from Iteration1 to Iteration2", changesTo.iterator().next().getMessage());
  }

  public void testStoryMovedAndChangedState() {
    final StoryStates old = new StoryStates();
    final StoryStates noo = new StoryStates();

    old.add("Foo", "Iteration1", StoryState.IMPLEMENTATION_REQUIRED);
    noo.add("Foo", "Iteration2", StoryState.IN_PROGRESS);

    final Set<LogEntry> changesTo = old.getChangesTo("Baz", noo);

    final Iterator<LogEntry> iterator = changesTo.iterator();
    assertEquals(2, changesTo.size());
    assertEquals("09Foo in 08Baz changed from ImplementationRequired to InProgress", iterator.next().getMessage());
    assertEquals("09Foo in 08Baz has moved from Iteration1 to Iteration2", iterator.next().getMessage());
  }

}
