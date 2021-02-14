package ch.ojp;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class WireMockExample {

    static final int PORT = 8888;

    @Rule
    public WireMockRule wm = new WireMockRule(PORT);

    @Test
    public void assertWireMockSetup() throws IOException {
        // Arrange - setup WireMock stubs
        configureFor("localhost", PORT);
        stubFor(get(urlEqualTo("/helloWord")).willReturn(
                aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("Hello World!")));
        // execute request through the http client
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("http://localhost:" + PORT + "/helloWorld").get().build();

        // Act - call the endpoint
        Response response = client.newCall(request).execute();

        // Assert - verify the response
        assertEquals(200, response.code());

    }

}