package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlProcessor;
import hexlet.code.util.Util;
import io.javalin.Javalin;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class AppTest {
    private static MockWebServer mockWebServer;
    private static Javalin app;
    private static String testUrl;

    private static String readTestPage() throws IOException {
        var is = AppTest.class.getClassLoader().getResourceAsStream("TestPage.html");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    @BeforeAll
    public static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        mockWebServer.enqueue(new MockResponse().setBody(readTestPage()).setResponseCode(200));
        testUrl = mockWebServer.url("/").toString();
    }

    @AfterAll
    public static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public final void startApp() throws IOException, SQLException {
        app = App.getApp();
    }
    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }
    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Сайты", "ID", "Имя", "Последняя проверка", "Код ответа");
        });
    }
    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/9999");
            assertThat(response.code()).isEqualTo(404);
        });
    }
    @Test
    void testUrlShowAndCheck() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url(UrlProcessor.normalizeUrl(testUrl));
            UrlsRepository.save(url);
            var response = client.get(NamedRoutes.urlPath(url.getId()));
            assertThat(response.body().string()).contains(UrlProcessor.normalizeUrl(testUrl));
            var response2 = client.post(NamedRoutes.checkUrlPath(url.getId()));
            assertThat(response2.body().string()).contains("Welcome", "200", "Web Page", "Example");
            var sqlData = UrlCheckRepository.getChecksById(url.getId()).getFirst();
            assertThat(sqlData.getH1()).isEqualTo("Welcome");
            assertThat(sqlData.getTitle()).isEqualTo("Web Page");
            assertThat(sqlData.getDescription()).isEqualTo("Example");

            var sqlData2 = UrlCheckRepository.getLatestChecks();
            var check = sqlData2.get(url.getId());
            var response4 = client.get(NamedRoutes.urlsPath());
            assertThat(response4.body().string()).contains(String.valueOf(check.getStatusCode()));

        });
    }

    @Test
    void testUrlCreation() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.body().string()).contains("https://example.com");
            var sqlData = UrlsRepository.getEntities().getFirst();
            assertThat(sqlData.getName()).isEqualTo("https://example.com");
        });
    }
    @Test
    void testUrlNotAdded() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            var response2 = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response2.body().string()).contains("https://example.com");

            var requestBody2 = "url=httpsrtt://examgdfgdfgple.comsdfdsfds";
            var response3 = client.post(NamedRoutes.urlsPath(), requestBody2);
            assertThat(response3.code()).isEqualTo(422);
        });
    }
    @Test
    void testLongNamesCut() {
        String testString = "1".repeat(202);
        assertThat(Util.textCutter(testString)).isEqualTo("1".repeat(200) + "...");
    }
}
