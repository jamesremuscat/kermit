package org.muscat.kermit.log.jira;

import java.util.List;

import junit.framework.TestCase;

import org.muscat.kermit.log.LogEntry;

public class TestJiraFeedParser extends TestCase {

  public void testParseFeed() throws Exception {
    final JiraFeedParser p = new JiraFeedParser();

    final List<LogEntry> entries = p.readFromURL(getClass().getResource("jira.xml").toExternalForm());

    assertEquals(10, entries.size());

    final LogEntry entry = entries.get(0);

    assertEquals("08JIRA: msww: Matthew Wightman commented on TNFC-1480 - TNFC causes tomcat to eventually become unresponsive for BNM: Results of this mornings investigation to attempt to reproduce the failure before trying the HEAD build:Trying to reproduce this by running a TNWSP locally (with TNFC 1.8.0), each time I repost the TNWSP config and then generate an empty form the JVM spawns a new daemon thread called &quot;tnfc.com.google.inject.internal.util.$Finalizer&quot;. The local tomcat logs have a number of lines like &quot;SEVERE: The web application [/tnwsp] appears to have started a thread named [tnfc.com.google.inject.internal.util.$Finalizer] but has failed to stop it. This is very likely to create a memory leak.&quot;After around 13-14 reposts of the TNWSP config locally, generating the forms starts taking a lot longer, with stack traces including tnfc.org.apache.poi.xssf.usermodel.XSSFSheet.getColumnWidth.", entry.getMessage());

  }

  public void testParseMA() throws Exception {
    final JiraFeedParser p = new JiraFeedParser();

    final List<LogEntry> entries = p.readFromURL(getClass().getResource("jira-ma.xml").toExternalForm());

    assertEquals(2, entries.size());

    final LogEntry entry1 = entries.get(0);
    final LogEntry entry2 = entries.get(1);

    assertEquals("08JIRA: ma: Matthew Allen commented on TNFC-1465 - TNSQLC subtly fails for unsupported databases: Verified as FIXED in r422114.", entry1.getMessage());
    assertEquals("08JIRA: ma: Matthew Allen changed the status to Verified on TNFC-1465 - TNSQLC subtly fails for unsupported databases.", entry2.getMessage());
  }

}
