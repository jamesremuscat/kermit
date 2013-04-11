package org.muscat.kermit.log.atom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.muscat.kermit.log.LogEntry;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parser for an Atom feed.
 * @author jrem
 */
public class AtomParser extends DefaultHandler {

  private static final String CHANGESET_TAG = "changeset-";

  private static final int UTF8_POUND_FIRST_BYTE = 0xC2;

  private static final int UTF8_POUND_SECOND_BYTE = 0xA3;

  /**
   * Default timeout for reading from RSS feed.
   */
  private static final int READ_TIMEOUT = 60000; // in milliseconds; one of your Earth minutes

  private final List<LogEntry> _changes = new LinkedList<LogEntry>();

  private String _revision;
  private String _url;
  private Date _date;
  private String _author;
  private String _message;

  private String _stringBuf;

  private boolean _inEntry = false;

  private String _revisionPrefix = "";

  private String _dateFormatString = "yyyy-MM-dd'T'HH:mm:ssXXX";

  private DateFormat _dateFormat = new SimpleDateFormat(_dateFormatString);

  public void setRevisionPrefix(final String prefix) {
    _revisionPrefix = prefix;
  }

  public void setDateFormat(final String dateFormatString) {
    _dateFormatString = dateFormatString;
    _dateFormat = new SimpleDateFormat(_dateFormatString);
  }

  @Override
  public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
    _stringBuf = "";
    if ("entry".equals(localName)) {
      _inEntry = true;
    }
  }

  @Override
  public void endElement(final String uri, final String localName, final String name) throws SAXException {
    if ("name".equals(localName)) {
      _author = _stringBuf;
    }
    else if ("summary".equals(localName) || "title".equals(localName)) {
      _message = _stringBuf;
    }
    else if ("id".equals(localName) && _inEntry) {
      _url = _stringBuf;
      if (_url.contains("revision=")) {
        _revision = _url.substring(_url.indexOf('?') + 1 + "revision=".length());
      }
      if (_url.contains(CHANGESET_TAG)) {
        _revision = _url.substring(_url.indexOf(CHANGESET_TAG)).substring(CHANGESET_TAG.length() + 1).substring(0, 12);
      }
    }
    else if ("updated".equals(localName)) {
      try {
        _date = _dateFormat.parse(_stringBuf);
      }
      catch (final ParseException e) {
        System.out.println("Parsing date " + _stringBuf + " failed against " + _dateFormatString);
        e.printStackTrace();
        _date = new Date();
      }
      catch (final NumberFormatException e) {
        System.out.println("Parsing date " + _stringBuf + " failed against " + _dateFormatString);
        e.printStackTrace();
        _date = new Date();
      }
    }
    else if ("entry".equals(localName)) {
      _changes.add(new AtomLogEntry(_revisionPrefix + _revision, _date, _author, _message, Collections.singleton(_url)));
      _inEntry = false;
    }

  }

  @Override
  public void characters(final char[] ch, final int start, final int length) throws SAXException {
    for (int i = start; i < length + start; i++) {
      _stringBuf += ch[i];
    }
  }

  public List<LogEntry> getChanges() {
    Collections.reverse(_changes);
    return Collections.unmodifiableList(_changes);
  }

  public List<LogEntry> readFromURL(final String url) throws SAXException, IOException {

    final URL actualUrl = new URL(url);

    final URLConnection connection = actualUrl.openConnection();
    connection.setReadTimeout(READ_TIMEOUT);
    final InputStream inputStream = connection.getInputStream();

    // Hacky fix to work around bug in Reviki

    final File tf = File.createTempFile(url.replaceAll("/", "").replaceAll(":", ""), ".tmp");
    try {
      final FileOutputStream out = new FileOutputStream(tf);

      try {
        int read = inputStream.read();
        int prev = -1;

        while (read != -1) {

          if (read == UTF8_POUND_SECOND_BYTE && prev != UTF8_POUND_FIRST_BYTE) {
            out.write(UTF8_POUND_FIRST_BYTE);
          }

          out.write(read);
          out.flush();
          prev = read;
          read = inputStream.read();
        }
        return read(new InputSource(new FileInputStream(tf)));
      }
      finally {
        out.close();
      }
    }
    finally {
      tf.delete();
    }

    // ~ Hacky fix

  }

  private List<LogEntry> read(final InputSource is) throws SAXException, IOException {

    final XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(this);
    reader.parse(is);

    return getChanges();
  }

}
