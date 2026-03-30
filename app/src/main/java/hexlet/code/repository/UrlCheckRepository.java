package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import java.sql.Timestamp;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, urlCheck.getUrlId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getTitle());
            preparedStatement.setString(5, urlCheck.getDescription());
            LocalDateTime createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(6, Timestamp.valueOf(createdAt));

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(createdAt);

            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
    public static List<UrlCheck> getChecksById(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ?";

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            List<UrlCheck> checks = new ArrayList<>();

            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

                var check =  new UrlCheck(id, statusCode, title, h1, description, urlId, createdAt);
                checks.add(check);
            }
            return checks;
        }
    }
    public static Map<Long, UrlCheck> getLatestChecks() throws SQLException {
        var sql = """
                SELECT DISTINCT
                    ON(url_id)
                    url_id,
                    status_code,
                    created_at
                FROM url_checks
                ORDER BY url_id, created_at DESC
                """;
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            Map<Long, UrlCheck> latestChecks = new HashMap<>();

            while (resultSet.next()) {
                var urlId = resultSet.getLong("url_id");
                var statusCode = resultSet.getInt("status_code");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

                var check =  new UrlCheck(statusCode);
                check.setCreatedAt(createdAt);
                latestChecks.put(urlId, check);
            }
            return latestChecks;
        }
    }
}
