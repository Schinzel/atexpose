package com.atexpose;

import com.atexpose.dispatcher.wrapper.FunnyChars;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 *
 * @author Schinzel
 */
public class AtExposeTest {
    AtExpose mAtExpose;
    private static final String LOCAL_HOST_IP = "127.0.0.1";
    private static final String URL = "http://" + LOCAL_HOST_IP + ":5555/call/";

    @Before
    public void before() {
        mAtExpose = AtExpose.create();
        mAtExpose.getWebServerBuilder()
                .numberOfThreads(5)
                .startWebServer();
    }


    @After
    public void after() {
        mAtExpose.shutdown();
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
        byte[] baMessage = request.getBytes(Charset.forName("Utf-8"));
        SocketRW.write(socket, baMessage);
        String expected = "HTTP/1.1 500 Internal Server Error\r\n"
                + "Server: AtExpose\r\n"
                + "Content-Length: 118\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Cache-Control: max-age=0\r\n"
                + "\r\n"
                + "Error while reading from socket. Request not allowed. Request has to start with GET or POST. Request:' " + request + "'";
        String response = SocketRW.read(socket);
        assertEquals(expected, response);
    }


    @Test
    public void test_WebServer_ghostCall() throws IOException {
        int port = 5555;
        Socket socket = new Socket(LOCAL_HOST_IP, port);
        byte[] baMessage = new byte[]{0};
        SocketRW.write(socket, baMessage);
        String expected = "HTTP/1.1 200 OK\r\n"
                + "Server: AtExpose\r\n"
                + "Content-Length: 8\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Cache-Control: max-age=0\r\n"
                + "\r\n"
                + "Hi Ghost";
        String response = SocketRW.read(socket);
        assertEquals(expected, response);
    }


    @Test
    public void test_WebServerCall_get_echo() throws IOException {
        String expected = "monkey";
        Connection.Response response = Jsoup
                .connect(URL +  "echo")
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


    @Expose(
    )
    public static String g() {
        return "It worked!";
    }


    @Test
    public void test_WebServerCall_shortMethodName() throws IOException {
        mAtExpose.getAPI().expose(new AtExposeTest());
        Connection.Response response = Jsoup
                .connect(URL + "g")
                .method(Connection.Method.GET)
                .execute();
        String result = response.body();
        assertEquals("It worked!", result);
    }

}
