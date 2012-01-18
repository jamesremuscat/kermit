package org.muscat.kermit;

import java.io.FileReader;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.muscat.kermit.svn.SVNLogListener;
import org.muscat.kermit.svn.SVNLogWatcher;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

public final class Kermit extends PircBot implements SVNLogListener {

  private static final int MAX_MESSAGE_LENGTH = 350;
  private final WatchedPathsConfig _paths = new WatchedPathsConfig();
  private final SVNLogWatcher _watcher;


  public Kermit(final String nick) throws SVNException {
    setName(nick);
    setLogin(nick);
    new Thread(_paths, "Path config watcher").start();
    _watcher = new SVNLogWatcher("https://svn-dev.int.corefiling.com/svn", _paths);
    new Thread(_watcher, "SVN log watcher").start();
    _watcher.addListener(this);
  }


  @Override
  public void logEntries(final Collection<SVNLogEntry> entries) {
    for (SVNLogEntry entry : entries) {
      for (String chan : getChannels()) {
        final String header = extractHeader(entry);
        final String path = extractPaths(entry);
        sendMessage(chan, header + extractLogMessage(entry));

        final long numSpaces = Math.round(Math.floor(Math.log10(entry.getRevision())) + 1);

        sendMessage(chan, StringUtils.spaces((int) (numSpaces + 1)) + Colors.DARK_GRAY + " in " + path + Colors.NORMAL);
      }
    }
  }


  /**
   * @param entry
   * @return
   */
  private String extractHeader(final SVNLogEntry entry) {
    return Colors.GREEN + "r" + entry.getRevision() + Colors.NORMAL + " by " + Colors.BOLD + entry.getAuthor() + Colors.NORMAL + ": ";
  }


  /**
   * @param entry
   * @return
   */
  private String extractPaths(final SVNLogEntry entry) {
    @SuppressWarnings("unchecked")
    final Set<String> changedPaths = entry.getChangedPaths().keySet();

    final String path;
    if (changedPaths.size() == 1) {
      path = (String) changedPaths.toArray()[0];
    }
    else if (changedPaths.size() > 1) {
      final String commonPrefix = StringUtils.getCommonPrefix(changedPaths.toArray(new String[1]));

      path = changedPaths.size() + " files under " + commonPrefix.substring(0, commonPrefix.lastIndexOf("/"));
    }
    else {
      path = "no files";
    }
    return path;
  }


  /**
   * @param entry
   * @return
   */
  private String extractLogMessage(final SVNLogEntry entry) {
    final String rawMessage = entry.getMessage().split("\n")[0];

    if (rawMessage.length() < MAX_MESSAGE_LENGTH) {
      return rawMessage;
    }
    else {
      return rawMessage.substring(0, MAX_MESSAGE_LENGTH) + "...";
    }
  }

  @Override
  protected void onJoin(final String channel, final String sender, final String login, final String hostname) {
    super.onJoin(channel, sender, login, hostname);
    sendMessage(channel, "Hey-ho everybody, the repository is at revision " + Colors.GREEN + _watcher.getLatestRevision() + Colors.NORMAL);
  }

  /**
   * @param args
   * @throws SVNException
   */
  public static void main(final String[] args) throws SVNException {

    // defaults that get overridden if they're in the properties file
    String nick = "Kermit";

    String server = "irc.int.corefiling.com";
    String channel = "#botTest";

    Properties properties = new Properties();
    try {
      properties.load(new FileReader("kermit.properties"));

      if (properties.containsKey("server")) {
        server = properties.getProperty("server");
      }

      if (properties.containsKey("channel")) {
        channel = properties.getProperty("channel");
      }

      if (properties.containsKey("nick")) {
        nick = properties.getProperty("nick");
      }

    }
    catch (Exception e1) {
      // do nothing, just use defaults
    }

    Kermit bot = new Kermit(nick);

    //bot.addTask(new DailySummaryTask(channel));

    try {

      bot.connect(server);

      bot.joinChannel(channel);

    }
    catch (Exception e) {
      System.out.println("Bad things");
      e.printStackTrace();
    }

  }

}
