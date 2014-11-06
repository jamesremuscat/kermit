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
  private final String _objectTitle;

  public JiraLogEntry(final String id, final String objectTitle, final String title, final String username, final String content, final String url) {
    _id = id;
    _objectTitle = objectTitle;
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
    final StringBuilder b = new StringBuilder();

    b.append(Colors.GREEN + _objectTitle + Colors.NORMAL + " in ");
    b.append(Colors.YELLOW + "JIRA" + Colors.NORMAL + " by ");
    b.append(Colors.BOLD + _username + Colors.NORMAL);
    b.append(": " + _title);
    if (!_content.isEmpty()) {
      b.append(": " + _content);
    }
    else {
      b.append(".");
    }

    return b.toString();
  }

  @Override
  public Set<String> getChangedPaths() {
    return Collections.singleton(_url);
  }

  @Override
  public int getPathSpaces() {
    return _objectTitle.length();
  }

}
