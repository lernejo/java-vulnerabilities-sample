package fr.lernejo.sqlinj.user.exception;

public class TooManyUsersWithTheSameLogin extends RuntimeException {
    public TooManyUsersWithTheSameLogin(String login) {
        super("Login: " + login);
    }
}
