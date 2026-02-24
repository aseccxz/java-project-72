package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.sql.SQLException;


public class App {
    private static String getDataBaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

    public static Javalin getApp() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDataBaseUrl());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            //config.fileRenderer(new JavalinJte());
        });

        app.get("/", ctx -> {
            ctx.result("Hello, World!");
        });

        return app;
    }

    public static void main(String[] args) throws SQLException {
        Javalin app = getApp();
        app.start(8080);
    }
}
