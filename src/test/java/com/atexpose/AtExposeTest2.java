package com.atexpose;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class AtExposeTest2 {
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void test_addScheduledReport_NO_SMTP() {
        String taskName = "theTaskName";
        String request = "ping";
        String timeOfDay = "14:30";
        AtExpose cc = new AtExpose();
        exception.expect(RuntimeException.class);
        exception.expectMessage("You need to set SMTP settings");
        cc.addScheduledReport(taskName, request, timeOfDay, "monkey@example.com", "fromName");
    }


    @Test
    public void test_addScheduledReport() throws JSONException {
        String taskName = "theTaskName";
        String request = "ping";
        String timeOfDay = "14:30";
        AtExpose atExpose = AtExpose.create();
        atExpose.setSMTPServerGmail("u1", "p1");
        atExpose.addScheduledReport(taskName, request, timeOfDay, "monkey@example.com", "fn1");
        JSONObject jo = atExpose.getState().getJson();
        JSONObject joDispatcher = jo.getJSONArray("Dispatchers").getJSONObject(0);
        assertEquals("ScheduledReport_" + taskName, joDispatcher.getString("Name"));
    }


    @Test
    public void testWebServerRedirects() throws IOException {
        int statusCode;
        String location;
        Connection.Response response;
        AtExpose.create().getWebServerBuilder()
                .numberOfThreads(5)
                .addFileRedirect("src.html", "dest.html")
                .addFileRedirect("dir1/dir2/src.html", "dirdest/dest.html")
                .startWebServer();
        //Basic test
        response = Jsoup
                .connect("http://127.0.0.1:5555/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("dest.html", location);
        //Test with dirs
        response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("dirdest/dest.html", location);
        //Test that query strings are passed on
        response = Jsoup
                .connect("http://127.0.0.1:5555/src.html?key1=val1")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("dest.html?key1=val1", location);
        //Test that query strings are passed on with dirs
        response = Jsoup
                .connect("http://127.0.0.1:5555/dir1/dir2/src.html?key2=val2")
                .method(Connection.Method.GET)
                .followRedirects(false)
                .execute();
        statusCode = response.statusCode();
        location = response.header("Location");
        assertEquals(302, statusCode);
        assertEquals("dirdest/dest.html?key2=val2", location);
    }

}
