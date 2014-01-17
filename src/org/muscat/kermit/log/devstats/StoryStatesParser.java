package org.muscat.kermit.log.devstats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class StoryStatesParser {

  public static StoryStates parse(final String url) {
    final StoryStates states = new StoryStates();

    try {
      final Document dom = getWebPageAsDOMDocument(url);

      final NodeList storyRows = dom.getElementsByTagName("tr");

      for (int i = 1; i < storyRows.getLength(); i++) { // start at 1 because first tr is the header row

        final Node storyRow = storyRows.item(i);

        final Node firstChild = storyRow.getFirstChild();

        final String colspan = firstChild.getAttributes().getNamedItem("colspan").getNodeValue();

        if ("td".equals(firstChild.getLocalName()) && "2".equals(colspan) && firstChild.hasChildNodes()) {
          final String storyName = firstChild.getChildNodes().item(1).getTextContent().trim();
          final String state = storyRow.getChildNodes().item(3).getChildNodes().item(0).getTextContent().trim();
          states.add(storyName, StoryState.fromStringForm(state));
        }


      }

    }
    catch (final Exception e) {
      // eh, we tried
      e.printStackTrace();
    }

    if (states.size() == 0) {
      System.out.println("WARNING Got no stories at " + url);
    }

    return states;
  }

  private static Document getWebPageAsDOMDocument(final String pageUrl) throws MalformedURLException, IOException, SAXException, TransformerFactoryConfigurationError, TransformerException {

    final URL url = new URL(pageUrl);
    final XMLReader reader = new Parser();
    reader.setFeature(Parser.namespacesFeature, false);
    reader.setFeature(Parser.namespacePrefixesFeature, false);

    final Transformer transformer = TransformerFactory.newInstance().newTransformer();

    final DOMResult result = new DOMResult();
    transformer.transform(new SAXSource(reader, new InputSource(url.openStream())),
        result);

    return result.getNode().getOwnerDocument();
  }

  public static void main(final String[] args) {
    parse("http://dev-reports.pd.test/story-stats/stories.cgi?project=magnify&view=by-phase&hide_complete=1");
  }

}
