package org.muscat.kermit;

import junit.framework.TestCase;

public class TestPathUtils extends TestCase {

  public void testActualCommits() {

    final String[] paths = {
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/results/ResultSourceLookup.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/results/impl/ResultSourceLookupImpl.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/config/ReportsEmptyWindowSessionConfigurationBuilder.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/ReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/AbstractReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/compound/CompoundReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/concept/ConceptReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/fact/FactReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/fbt/FactsByTableReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/tree/ConceptWithRelationshipTreeReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/definition/impl/types/tree/FactWithRelationshipTreeReportType.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/editor/impl/AvailableFieldsTreeModel.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/editor/model/impl/ReportDefinitionEditorModelExtensionsImpl.java",
        "devel/mwtms/trunk/reports.ui/src/com/corefiling/spidermonkey/reports/ui/results/impl/RunReportUIImpl.java",
        "devel/mwtms/trunk/reports.ui/unitTests/src/com/corefiling/spidermonkey/reports/ui/editor/model/impl/TestReportDefinitionEditorModelExtensionsImpl.java",
        "devel/mwtms/trunk/reports.ui/unitTests/src/com/corefiling/spidermonkey/reports/ui/results/impl/TestRunReportUIImpl.java"
    };

    assertEquals("devel/mwtms/trunk/reports.ui", PathUtils.extractLongestCommonParentPath(paths));

    final String[] nico = {
        "usr/njlgad/SeahorsesStatusPage/css/",
        "usr/njlgad/SeahorsesStatusPage/css/style.css",
        "usr/njlgad/SeahorsesStatusPage/index.html"
    };
    assertEquals("usr/njlgad/SeahorsesStatusPage", PathUtils.extractLongestCommonParentPath(nico));
  }

  public void testSingleFile() {
    final String[] paths = {
        "aaaa/bbbb/aaaa/abcd.efg"
    };

    assertEquals("aaaa/bbbb/aaaa/abcd.efg", PathUtils.extractLongestCommonParentPath(paths));
  }

  public void testSimplePaths() {
    final String[] paths = {
        "aaaa/bbbb/aaaa/abcd.efg",
        "aaaa/bbbb/aaaa/hijk.lmo"
    };

    assertEquals("aaaa/bbbb/aaaa", PathUtils.extractLongestCommonParentPath(paths));

    final String[] paths2 = {
        "aaaa/bbbb/aaaa/abcd.efg",
        "aaaa/bbbb/aaaa/abcd.lmo"
    };

    assertEquals("aaaa/bbbb/aaaa", PathUtils.extractLongestCommonParentPath(paths2));

    final String[] lengths = {
        "aaaa/bbbb/something/",
        "aaaa/bbbb/something/somethingElse"
    };
    assertEquals("aaaa/bbbb/something", PathUtils.extractLongestCommonParentPath(lengths));
  }

  public void testSharedPathPrefix() {
    final String[] paths = {
        "aaaa/bbbb/first/aaaa/bbbb/abcd.efg",
        "aaaa/bbbb/second/aaaa/bbbb/hijk.lmo"
    };

    assertEquals("aaaa/bbbb", PathUtils.extractLongestCommonParentPath(paths));
  }

}
