package com.atexpose;

import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.PropertiesDispatcher;
import com.atexpose.dispatcher_factories.WebServerBuilder;
import io.schinzel.basicutils.FunnyChars;
import io.schinzel.basicutils.Sandman;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class WebServerTest {
    private AtExpose mAtExpose;
    private static final String LOCAL_HOST_IP = "127.0.0.1";
    private static final String URL = "http://" + LOCAL_HOST_IP + ":5555/api/";


    @Before
    public void before() {
        Sandman.snoozeMillis(10);
        IDispatcher webServer = WebServerBuilder.create()
                .numberOfThreads(5)
                .build();
        mAtExpose = AtExpose.create()
                .start(webServer);
    }


    @After
    public void after() {
        mAtExpose.shutdown();
        //Snooze required to get tests to work on Travis
        Sandman.snoozeMillis(10);
    }


    @Test
    public void test_WebServer_ping() throws IOException {
        Connection.Response response = Jsoup
                .connect(URL + "ping")
                .method(Connection.Method.GET)
                .execute();
        String result = response.body();
        assertEquals("pong", result);
    }


    @Test
    public void test_WebServer_noHeader() throws IOException {
        int port = 5555;
        Socket socket = new Socket(LOCAL_HOST_IP, port);
        String request = "no header call";
        byte[] baMessage = request.getBytes(StandardCharsets.UTF_8);
        SocketRWUtil.write(socket, baMessage);
        String expected = "Error while reading from socket. Request not allowed. Request has to start with GET or POST.";
        String response = SocketRWUtil.read(socket);
        assertThat(response).contains(expected);
    }


    @Test
    public void test_WebServer_ghostCall() throws IOException {
        int port = 5555;
        Socket socket = new Socket(LOCAL_HOST_IP, port);
        byte[] baMessage = new byte[]{0};
        SocketRWUtil.write(socket, baMessage);
        String expected = "HTTP/1.1 200 OK\r\n"
                + "Server: " + PropertiesDispatcher.RESP_HEADER_SERVER_NAME + "\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Cache-Control: max-age=0\r\n"
                + "Content-Length: 9\r\n"
                + "\r\n"
                + "Hi Ghost!";
        String response = SocketRWUtil.read(socket);
        assertEquals(expected, response);
    }


    @Test
    public void test_WebServerCall_get_echo() throws IOException {
        String expected = "monkey";
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.GET)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);
    }


    @Test
    public void test_WebServerCall_post_echo() throws IOException {
        String expected = "monkey";
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.POST)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);
    }


    @Test
    public void test_WebServerCall_post_echo_encSSN() throws IOException {
        String expected = "v06_FvaZo6U0H52jqh3V/R/wTQ==";
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.POST)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);
    }


    @Test
    public void test_WebServerCall_post_funnyChars() throws IOException {
        for (FunnyChars funnyChars : FunnyChars.values()) {
            String expected = funnyChars.getString();
            Connection.Response response = Jsoup
                    .connect(URL + "echo")
                    .method(Connection.Method.POST)
                    .data("String", expected)
                    .execute();
            String result = response.body();
            assertEquals(expected, result);
        }
    }


    @Test
    public void test_WebServerCall_post_longString() throws IOException {
        //Create string that is 10000 long
        StringBuilder sb = new StringBuilder();
        String s = "0123456789";
        for (int i = 0; i < 1000; i++) {
            sb.append(s);
        }
        String expected = sb.toString();
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.POST)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);
    }


    @Test
    public void test_WebServerCall_get_ShortArgValue() throws IOException {
        String expected = "g";
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.GET)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);
    }


    @Test
    public void test_WebServerCall_post_shortString() throws IOException {
        String expected = "monkey";
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.POST)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);

    }


    @Test
    public void test_WebServerCall_get_longString() throws IOException {
        //Create string that is 10000 long
        StringBuilder sb = new StringBuilder();
        String s = "0123456789";
        for (int i = 0; i < 1000; i++) {
            sb.append(s);
        }
        String expected = sb.toString();
        Connection.Response response = Jsoup
                .connect(URL + "echo")
                .method(Connection.Method.GET)
                .data("String", expected)
                .execute();
        String result = response.body();
        assertEquals(expected, result);
    }


    @SuppressWarnings({"unused", "RedundantSuppression"})
    @Expose
    public static String g() {
        return "It worked!";
    }


    @Test
    public void test_WebServerCall_shortMethodName() throws IOException {
        mAtExpose.getAPI().expose(new WebServerTest());
        Connection.Response response = Jsoup
                .connect(URL + "g")
                .method(Connection.Method.GET)
                .execute();
        String result = response.body();
        assertEquals("It worked!", result);
    }

}
