package org.muscat.kermit.log.tntfl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jibble.pircbot.Colors;
import org.muscat.kermit.WatchedPath;
import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.LogWatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class TNTFLWatcher extends LogWatcher {

  private long _mostRecentGame = 0;

  public TNTFLWatcher(final WatchedPath path) {
    super(path);
    for (final SubmittedGame g: getRecentGames(path.getPath())) {
      _mostRecentGame = Math.max(_mostRecentGame, g.getDateTime().getTime());
    }
  }

  @Override
  protected String getThreadComment() {
    return "TNTFL watcher for " + getPath();
  }

  @Override
  protected void checkUpdates() {
    final List<SubmittedGame> newGames = getRecentGames(getPath().getPath());

    final Set<LogEntry> newScores = new LinkedHashSet<LogEntry>();

    for (final SubmittedGame g : newGames) {
      if (g.getDateTime().getTime() > _mostRecentGame) {
        newScores.add(new FinalScore(g));
        _mostRecentGame = g.getDateTime().getTime();
      }
    }

    notifyAllListeners(newScores);

  }

  static List<SubmittedGame> getRecentGames(final Reader r) {
    final GsonBuilder gb = new GsonBuilder();
    gb.registerTypeAdapter(SubmittedGame.class, new SubmittedGame.SubmittedGameDeserializer());

    final Gson g = gb.create();

    try {
      final List<SubmittedGame> result = g.fromJson(r, new TypeToken<List<SubmittedGame>>(){ /* */ }.getType());
      r.close();
      return result;
    }
    catch (final JsonSyntaxException e) {
      e.printStackTrace();
    }
    catch (final IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  private static List<SubmittedGame> getRecentGames(final String url) {

    try {
      final URL actualUrl = new URL(url);

      final URLConnection connection = actualUrl.openConnection();
      final InputStream inputStream = connection.getInputStream();

      return getRecentGames(new InputStreamReader(inputStream));
    }
    catch (final IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }

  private static class FinalScore implements LogEntry {

    private final SubmittedGame _game;

    public FinalScore(final SubmittedGame game) {
      _game = game;

    }

    @Override
    public String getChangeID() {
      return Long.toString(_game.getDateTime().getTime());
    }

    @Override
    public String getMessage() {
      final StringBuilder b = new StringBuilder();

      final int totalGoals = _game.getBlueScore() + _game.getRedScore();

      b.append(Colors.GREEN + "FINAL SCORE: ");
      if (_game.getRedScore() == totalGoals) {
        b.append(Colors.YELLOW + _game.getRedPlayer());
        b.append(" " + _game.getRedScore() + Colors.NORMAL);
      }
      else {
        b.append(Colors.RED + _game.getRedPlayer());
        b.append(Colors.NORMAL + " " + _game.getRedScore());
      }
      b.append(" - ");

      if (_game.getBlueScore() == totalGoals) {
        b.append (Colors.YELLOW + _game.getBlueScore() + " ");
        b.append(_game.getBluePlayer() + Colors.NORMAL);
      }
      else {
        b.append (_game.getBlueScore() + " ");
        b.append(Colors.BLUE + _game.getBluePlayer() + Colors.NORMAL);
      }

      b.append(" (Skill change ");

      if (_game.getSkillChangeDirection() == Player.RED) {
        b.append(Colors.RED);
      }
      else {
        b.append(Colors.BLUE);
      }
      b.append(String.format("%.3f", _game.getSkillChange()));

      b.append(Colors.NORMAL + ")");
      return b.toString();

    }

    @Override
    public Set<String> getChangedPaths() {
      return Collections.emptySet();
    }

    @Override
    public int getPathSpaces() {
      return 0;
    }

  }

}
