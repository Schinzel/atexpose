package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.parser.urlparser.Redirect.RedirectType;
import org.apache.commons.lang3.tuple.Pair;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Jorgen Andersson <jorgen@kollektiva.se>
 */
public class RedirectTest {

    @Test
    public void testTemporaryHostRedirect() {

        Redirect redirect = new Redirect("myexample.com", "www.example.com", RedirectType.HOST, RedirectHttpStatus.TEMPORARY);

        assertTrue(redirect.isRedirectHost("myexample.com"));
        assertFalse(redirect.isRedirectHost("www.myexample.com"));
        assertFalse(redirect.isRedirectHost("websites/site/home.html"));

        assertFalse(redirect.isRedirectFile("myexample.com"));
        assertFalse(redirect.isRedirectFile("www.myexample.com"));
        assertFalse(redirect.isRedirectFile("websites/site/home.html"));

        Pair<String, RedirectHttpStatus> info = redirect.getRedirectInfo(false, "websites/sub/home.html");
        assertEquals("http://www.example.com/websites/sub/home.html", info.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, info.getRight());

        Pair<String, RedirectHttpStatus> infoQuery = redirect.getRedirectInfo(false, "websites/dir/home.html?xyz=test");
        assertEquals("http://www.example.com/websites/dir/home.html?xyz=test", infoQuery.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, infoQuery.getRight());
    }


    @Test
    public void testPermanentHostRedirect() {

        Redirect redirect = new Redirect("myexample.com", "www.example.com", RedirectType.HOST, RedirectHttpStatus.PERMANENT);

        assertTrue(redirect.isRedirectHost("myexample.com"));
        assertFalse(redirect.isRedirectHost("www.myexample.com"));
        assertFalse(redirect.isRedirectHost("websites/dir/home.html"));

        assertFalse(redirect.isRedirectFile("myexample.com"));
        assertFalse(redirect.isRedirectFile("www.myexample.com"));
        assertFalse(redirect.isRedirectFile("websites/dir/home.html"));

        Pair<String, RedirectHttpStatus> info = redirect.getRedirectInfo(false, "websites/dir/home.html");
        assertEquals("http://www.example.com/websites/dir/home.html", info.getLeft());
        assertEquals(RedirectHttpStatus.PERMANENT, info.getRight());

        Pair<String, RedirectHttpStatus> infoQuery = redirect.getRedirectInfo(false, "websites/dir/home.html?xyz=test");
        assertEquals("http://www.example.com/websites/dir/home.html?xyz=test", infoQuery.getLeft());
        assertEquals(RedirectHttpStatus.PERMANENT, infoQuery.getRight());
    }


    @Test
    public void testTemporaryFileRedirect() {

        Redirect redirect = new Redirect("websites/site/home.html", "websites/site/index.html", RedirectType.FILE, RedirectHttpStatus.TEMPORARY);

        assertFalse(redirect.isRedirectHost("myexample.com"));
        assertFalse(redirect.isRedirectHost("www.myexample.com"));
        assertFalse(redirect.isRedirectHost("websites/site/home.html"));

        assertFalse(redirect.isRedirectFile("myexample.com"));
        assertFalse(redirect.isRedirectFile("www.myexample.com"));
        assertTrue(redirect.isRedirectFile("websites/site/home.html"));

        Pair<String, RedirectHttpStatus> info = redirect.getRedirectInfo(false, "websites/site/home.html");
        assertEquals("websites/site/index.html", info.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, info.getRight());

        Pair<String, RedirectHttpStatus> infoQuery = redirect.getRedirectInfo(false, "websites/site/home.html?xyz=test");
        assertEquals("websites/site/index.html?xyz=test", infoQuery.getLeft());
        assertEquals(RedirectHttpStatus.TEMPORARY, infoQuery.getRight());
    }

}
