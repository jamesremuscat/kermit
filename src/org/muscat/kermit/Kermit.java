package org.muscat.kermit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;
import org.muscat.kermit.log.LogEntry;
import org.muscat.kermit.log.LogListener;
import org.muscat.kermit.subscriptions.SubscriptionStore;

public final class Kermit extends PircBot implements LogListener {

  private static final int MAX_MESSAGE_LENGTH = 350;
  private static final int MAX_SUBSCRIPTION_MSG_LINES = 5;

  private final WatchedPathsConfig _paths;

  private final SubscriptionStore _subscriptions = SubscriptionStore.Factory.getSerializableStore("KermitSubscriptions");

  public Kermit(final String nick) {
    setName(nick);
    setLogin(nick);
    _paths = new WatchedPathsConfig(this);
    new Thread(_paths, "Path config watcher").start();
  }

  @Override
  protected void onPrivateMessage(final String sender, final String login, final String hostname, final String message) {
    if (message.startsWith("!sub ")) {
      final String pageName = message.substring("!sub ".length());
      _subscriptions.add(pageName, sender);
      sendMessage(sender, "You are now subscribed to " + pageName);
    }
    else if (message.startsWith("!unsub ")) {
      final String pageName = message.substring("!unsub ".length());
      _subscriptions.remove(pageName, sender);
      sendMessage(sender, "You are no longer subscribed to " + pageName);
    }
    else if (message.startsWith("!list")) {
      sendMessage(sender, getSubscriptionListMessage(sender));
    }
    else if (message.startsWith("!version")) {
      sendMessage(sender, getVersionMessage());
    }
    else {
      sendMessage(sender, getHelpMessage());
    }

  }

  private String getVersionMessage() {

    String revisionInfo = "unknown";
    try {
      final InputStream versionSource = getClass().getResourceAsStream("version.txt");
      if (versionSource.available() > 0) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(versionSource));
        revisionInfo = reader.readLine();
      }
    }
    catch (final IOException e) {
      // well, we tried...
      e.printStackTrace();
    }
    return "Kermit rev. " + revisionInfo;
  }

  private static String[] getHelpMessage() {
    final String[] msg = new String[] {
        "Kermit will respond to the following by /msg:",
        "!sub WikiPageName     Subscribe to a page",
        "!unsub WikiPageName   Unsubscribe from a page",
        "!list                 List your current subscriptions",
        "!version              Show version of Kermit",
        "Note, subscriptions are by nick; nick changes are not acknowledged."
    };
    return msg;
  }

  /**
   * @param sender
   * @return
   */
  private String[] getSubscriptionListMessage(final String sender) {
    final Queue<String> subs = new ArrayDeque<String>(_subscriptions.getAllForUser(sender));

    final String[] response = new String[MAX_SUBSCRIPTION_MSG_LINES];
    response[0] = "You are subscribed to: ";
    int i = 0;

    while (i < MAX_SUBSCRIPTION_MSG_LINES && !subs.isEmpty()) {
      if (response[i].length() + subs.peek().length() + 2 >= getMaxLineLength() - 2) {
        i++;
      }
      response[i] += subs.poll() + ", ";
    }

    response[i] = response[i].substring(0, response[i].length() - 2);
    return response;
  }

  /**
   * Helper method that sends multiple messages (or a multi-line message) at
   * once.
   *
   * @param target Destination of message (channel or user)
   * @param messages Array of messages to send
   */
  protected void sendMessage(final String target, final String[] messages) {
    for (final String m : messages) {
      if (m != null) {
        sendMessage(target, m);
      }
    }
  }

  @Override
  public synchronized void logEntries(final Collection<LogEntry> entries) {
    for (final LogEntry entry : entries) {
      for (final String chan : getChannels()) {
        sendMessage(chan, limitMessageLength(entry.getMessage()));

        if (!entry.getChangedPaths().isEmpty()) {
          final String path = extractPaths(entry);
          final long numSpaces = entry.getPathSpaces();

          sendMessage(chan, StringUtils.spaces((int) (numSpaces + 1)) + Colors.DARK_GRAY + "in " + path + Colors.NORMAL);
        }
      }
    }
  }



  /**
   * @param entry
   * @return
   */
  private String extractPaths(final LogEntry entry) {
    final Set<String> changedPaths = entry.getChangedPaths();
    try {

      final String path;
      if (changedPaths.size() == 1) {
        path = (String) changedPaths.toArray()[0];
      }
      else if (changedPaths.size() > 1) {
        final String commonPrefix = PathUtils.extractLongestCommonParentPath(changedPaths.toArray(new String[1]));

        path = changedPaths.size() + " files under " + commonPrefix;
      }
      else {
        path = "no files";
      }
      return path;
    }
    catch (final ArrayIndexOutOfBoundsException e) {
      System.out.println("Failed extracting paths for " + entry.getChangeID());
      System.out.println(Arrays.toString(changedPaths.toArray()));
      throw e;
    }
  }

  /**
   * Limit a string to a given length (plus three for the ... that gets appended if we truncate).
   */
  private static String limitMessageLength(final String rawMessage) {
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
    if (sender.equals(getNick())) {
      sendMessage(channel, "Hey-ho everybody!");
    }
  }

  /**
   * @param args
   */
  public static void main(final String[] args) {

    // defaults that get overridden if they're in the properties file
    String nick = "Kermit";

    String server = "irc.int.corefiling.com";
    String channel = "#kermit";

    final Properties properties = new Properties();
    try {
      properties.load(new FileReader("kermit.properties"));

      if (properties.containsKey("server")) {
        server = properties.getProperty("server");
        System.out.println("Set server to " + server);
      }

      if (properties.containsKey("channel")) {
        channel = properties.getProperty("channel");
        System.out.println("Set channel to " + channel);
      }

      if (properties.containsKey("nick")) {
        nick = properties.getProperty("nick");
        System.out.println("Set nick to " + nick);
      }

    }
    catch (final Exception e1) {
      // do nothing, just use defaults
      e1.printStackTrace();
      System.out.println("Exception while loading properties, using defaults instead.");
    }

    final Kermit bot = new Kermit(nick);

    try {
      bot.connect(server);
      bot.joinChannel(channel);
    }
    catch (final Exception e) {
      System.out.println("Bad things");
      e.printStackTrace();
    }

  }

}
