package org.muscat.kermit.log.jira;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.muscat.kermit.log.LogEntry;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class JiraFeedParser extends DefaultHandler {

  private static final String ACTIVITY_NAMESPACE = "http://activitystrea.ms/spec/1.0/";


  private static final String ENTRY = "entry";


  /**
   * Default timeout for reading from RSS feed.
   */
  private static final int READ_TIMEOUT = 60000; // in milliseconds; one of your Earth minutes


  private final List<LogEntry> _changes = new LinkedList<LogEntry>();

  private boolean _inEntry = false;
  private boolean _inTarget = false;
  private String _stringBuf;
  private String _id;
  private String _title;
  private String _username;
  private String _content;
  private String _url;
  private String _objectTitle;


  public List<LogEntry> getChanges() {
    Collections.reverse(_changes);
    return Collections.unmodifiableList(_changes);
  }


  public List<LogEntry> readFromURL(final String url) throws SAXException, IOException {

    final URL actualUrl = new URL(url);

    final URLConnection connection = actualUrl.openConnection();
    connection.setReadTimeout(READ_TIMEOUT);
    final InputStream inputStream = connection.getInputStream();

    return read(new InputSource(inputStream));

  }

  private List<LogEntry> read(final InputSource is) throws SAXException, IOException {

    final XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(is);

    return getChanges();
  }

  @Override
  public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
    _stringBuf = "";
    if (ENTRY.equals(localName)) {
      _inEntry = true;
      _title = "";
      _objectTitle = "";
      _id = "";
      _username = "";
      _content = "";
    }
    else if (("target".equals(localName) || "object".equals(localName)) && ACTIVITY_NAMESPACE.equals(uri)) {
      _inTarget = true;
    }
    else {
      if (_inTarget) {
        if ("link".equals(localName)) {
          _url = attributes.getValue("href");
        }
      }
    }
  }

  @Override
  public void endElement(final String uri, final String localName, final String qName) throws SAXException {
    if (ENTRY.equals(localName)) {
      _inEntry = false;
      _changes.add(new JiraLogEntry(_id, _objectTitle, _title, _username, _content, _url));
    }
    else if (("target".equals(localName) || "object".equals(localName)) && ACTIVITY_NAMESPACE.equals(uri)) {
      _inTarget = false;
    }
    if (_inEntry && !_inTarget) {
      if ("title".equals(localName)) {
        _title = prepareHTMLContent(_stringBuf);
      }
      if ("id".equals(localName)) {
        _id = _stringBuf;
      }
      if ("username".equals(localName)) {
        _username = _stringBuf;
      }
      if ("content".equals(localName)) {
        _content = prepareHTMLContent(_stringBuf);
      }
    }
    if (_inTarget) {
      if ("title".equals(localName)) {
        _objectTitle = prepareHTMLContent(_stringBuf);
      }
    }
  }

  private static String prepareHTMLContent(final String html) {
    return StringEscapeUtils.unescapeXml(html.replaceAll("<[^>]*>", "").replaceAll("\n+", " ").replaceAll(" +", " ").trim());
  }

  @Override
  public void characters(final char[] ch, final int start, final int length) throws SAXException {
    for (int i = start; i < length + start; i++) {
      _stringBuf += ch[i];
    }
  }

}
