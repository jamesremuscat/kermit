package org.muscat.kermit.log.tntfl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

public class TestTNTFLFeedParser extends TestCase {

  public void testParseFeed() throws Exception {

    final InputStream testData = getClass().getResourceAsStream("recent.json");
    final List<SubmittedGame> entries = TNTFLWatcher.getRecentGames(new InputStreamReader(testData));

    assertEquals(2, entries.size());

    final SubmittedGame game = entries.get(0);

    assertEquals("sjs", game.getBluePlayer());
    assertEquals("tmm", game.getRedPlayer());

    assertEquals(Player.RED, game.getSkillChangeDirection());

    assertEquals(1, game.getRankChange(Player.RED));
    assertEquals(0, game.getRankChange(Player.BLUE));

    assertEquals(13, game.getNewRank(Player.RED));
    assertEquals(10, game.getNewRank(Player.BLUE));
  }

}
