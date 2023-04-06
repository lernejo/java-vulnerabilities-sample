package fr.lernejo.sqlinj.inspect;

import fr.lernejo.sqlinj.user.UserService;
import fr.lernejo.sqlinj.user.exception.UnauthorizedUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Do not do this in production.<br/>
 * The sole purpose of an application is to <b>not have</b> full access to the database.
 */
@RestController
class InspectionController {

    private final UserService userService;
    private final DataSource dataSource;

    InspectionController(UserService userService, DataSource dataSource) {
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @PostMapping("/api/inspect")
    SqlResult executeSqlQuery(@RequestHeader("Authorization") String authorizationHeader, @RequestBody String sqlQuery) {
        if (!userService.isAuthenticated(authorizationHeader)) {
            throw new UnauthorizedUser();
        }
        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            return mapFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private SqlResult mapFromResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        List<String> headers = new ArrayList<>();
        for (var columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            headers.add(metaData.getColumnName(columnIndex));
        }

        List<List<String>> rows = new ArrayList<>();
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (var columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
                row.add(resultSet.getString(columnIndex));
            }
            rows.add(row);
        }

        return new SqlResult(headers, rows);
    }
}
