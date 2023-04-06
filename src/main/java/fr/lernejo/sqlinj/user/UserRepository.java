package fr.lernejo.sqlinj.user;

import fr.lernejo.sqlinj.user.dto.UserEntity;
import fr.lernejo.sqlinj.user.exception.TooManyUsersWithTheSameLogin;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
class UserRepository {

    private final DataSource dataSource;

    UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    UserEntity createUser(UserEntity userEntity) {
        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO \"user\"(login, encoded_password, first_name, last_name) VALUES ('"
                + userEntity.login() + "', '"
                + userEntity.encodedPassword() + "', '"
                + userEntity.firstName() + "', '"
                + userEntity.lastName() + "')"
            );
            return userEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Optional<UserEntity> findUserByLogin(String login) throws TooManyUsersWithTheSameLogin {
        try (var connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM \"user\" WHERE login = '" + login + "'")) {
            List<UserEntity> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapToEntity(resultSet));
            }
            if (users.size() == 0) {
                return Optional.empty();
            } else if (users.size() > 1) {
                throw new TooManyUsersWithTheSameLogin(login);
            }
            return Optional.of(users.get(0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private UserEntity mapToEntity(ResultSet resultSet) {
        try {
            return new UserEntity(
                resultSet.getString("login"),
                resultSet.getString("encoded_password"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Unable to read information from resultset", e);
        }
    }
}
