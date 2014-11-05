package org.muscat.kermit.log.jira;

import java.util.Collections;
import java.util.Set;

import org.jibble.pircbot.Colors;
import org.muscat.kermit.log.LogEntry;

public class JiraLogEntry implements LogEntry {

  private final String _id;
  private final String _title;
  private final String _username;
  private final String _content;
  private final String _url;

  public JiraLogEntry(final String id, final String title, final String username, final String content, final String url) {
    _id = id;
    _title = title;
    _username = username;
    _content = content;
    _url = url;
  }

  @Override
  public String getChangeID() {
    return _id;
  }

  @Override
  public String getMessage() {
    return Colors.GREEN + "JIRA: " + Colors.NORMAL + Colors.BOLD + _username + Colors.NORMAL + ": " + _title + ": " + _content;
  }

  @Override
  public Set<String> getChangedPaths() {
    return Collections.singleton(_url);
  }

}
