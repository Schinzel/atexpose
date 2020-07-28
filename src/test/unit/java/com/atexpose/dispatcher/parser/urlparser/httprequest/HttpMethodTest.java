package com.atexpose.dispatcher.parser.urlparser.httprequest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;


public class HttpMethodTest {

    @Test
    public void getRequestMethod_GetRequest_Get() {
        String httpRequest = "GET /index.html?xyz=1234 HTTP/1.1\r\n"
                + "Host: 127.0.0.1:5555\r\n"
                + "Connection: keep-alive\r\n"
                + "Pragma: no-cache\r\n"
                + "Pragma2:\r\n"
                + "Pragma3: This:is:the:value\r\n"
                + "Cache-Control: no-cache\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
                + "Accept-Encoding: gzip, deflate, sdch\r\n"
                + "Accept-Language: en-US,en;q=0.8\r\n"
                + "Cookie: db=19710101; ci=0733787878; __distillery=v20150227_0d85f699-344b-49d2-96e2-c0a072b93bb3; _gat=1; _ga=GA1.1.921947710.1426063424; ptl=0; undefined=0; cp=0\r\n"
                + "\r\n"
                + "The body\r\n";
        HttpMethod requestMethod = HttpMethod.getRequestMethod(httpRequest);
        assertThat(requestMethod).isEqualByComparingTo(HttpMethod.GET);
    }


    @Test
    public void getRequestMethod_PostRequest_Post() {
        String httpRequest = "POST /api/getDataFromPM HTTP/1.1\r\n"
                + "Host: 127.0.0.1:5555\r\n"
                + "Connection: keep-alive\r\n"
                + "Content-Length: 18\r\n"
                + "Accept: */*\r\n"
                + "Origin: http://127.0.0.1:5555\r\n"
                + "X-Requested-With: XMLHttpRequest\r\n"
                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36\r\n"
                + "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
                + "Referer: http://127.0.0.1:5555/\r\n"
                + "Accept-Encoding: gzip, deflate\r\n"
                + "Accept-Language: en-US,en;q=0.8\r\n"
                + "Cookie: db=19710728; ci=+46733759593; _ga=GA1.1.957030889.1423688797; _gat=1; cp=1; ptl=2; undefined=1\r\n"
                + "\r\n"
                + "name=John&time=2pm";
        HttpMethod requestMethod = HttpMethod.getRequestMethod(httpRequest);
        assertThat(requestMethod).isEqualByComparingTo(HttpMethod.POST);
    }


    @Test
    public void getAsString_Get_GET() {
        assertThat(HttpMethod.GET.getAsString()).isEqualTo("GET /");
    }


    @Test
    public void getAsString_Post_GOST() {
        assertThat(HttpMethod.POST.getAsString()).isEqualTo("POST /");
    }
}