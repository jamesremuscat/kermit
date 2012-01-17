package org.muscat.kermit.svn;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

public class History {

  /**
   * @param args
   */
  public static void main(final String[] args) {
    DAVRepositoryFactory.setup( );

    String url = "https://svn-dev.int.corefiling.com/svn";
    long startRevision = -1;
    long endRevision = 250000;

    SVNRepository repository = null;
    try {
      repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
      @SuppressWarnings("unchecked")
      Collection<SVNLogEntry> log = repository.log( new String[] { "devel/56RK" } , null , startRevision , endRevision , true , true );
      for(SVNLogEntry e : log) {
        System.out.println(e.getRevision() + " " + e.getAuthor() + " " + e.getMessage().split("\n")[0]);
      }
    }
    catch (SVNException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
