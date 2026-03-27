package hexlet.code.controller;

import hexlet.code.dto.urls.BuildUrlPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlProcessor;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import java.sql.SQLException;
import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {
    public static void root(Context ctx) {
        var page = new BuildUrlPage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("index.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        String name = ctx.formParamAsClass("url", String.class).get();
        try {
            String domain = UrlProcessor.normalizeUrl(name);
            Url url = new Url(domain);
            var presentUrl = UrlsRepository.findUrl(url);
            if (presentUrl.isPresent()) {
                url = presentUrl.get();
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "fail");
            } else {
                UrlsRepository.save(url);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "success");
            }
            ctx.redirect(NamedRoutes.urlPath(url.getId()));

        } catch (Exception e) {
            var page = new BuildUrlPage(name);
            page.setFlash(e.getMessage());
            page.setFlashType("fail");
            ctx.render("index.jte", model("page", page)).status(422);
        }
    }
    public static void index(Context ctx) throws SQLException {
        var urls = UrlsRepository.getEntities();
        var urlsPage = new UrlsPage(urls, UrlCheckRepository.getLatestChecks());
        urlsPage.setFlash(ctx.consumeSessionAttribute("flash"));
        urlsPage.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/index.jte", model("page", urlsPage));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Страница не найдена"));
        var urlChecks = UrlCheckRepository.getChecksById(id);
        var urlPage = new UrlPage(url, urlChecks);
        urlPage.setFlash(ctx.consumeSessionAttribute("flash"));
        urlPage.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/show.jte", model("page", urlPage));
    }
    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            var statusCode = response.getStatus();

            if (statusCode >= 400 && statusCode <= 599) {
                throw new Exception("Bad status code");
            }

            var responseBody = Jsoup.parse(response.getBody());
            var title = responseBody.title();

            var element = responseBody.selectFirst("h1");
            var h1 = element == null ? "" : element.text();

            element = responseBody.selectFirst("meta[name=description]");
            var description = element == null ? "" : element.attr("content");

            var urlCheck = new UrlCheck(statusCode, title, h1, description);
            urlCheck.setUrlId(url.getId());
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Произошла ошибка при проверке " + e.getMessage());
            ctx.sessionAttribute("flashType", "fail");
        }
        ctx.redirect(NamedRoutes.urlPath(id));
    }
}
