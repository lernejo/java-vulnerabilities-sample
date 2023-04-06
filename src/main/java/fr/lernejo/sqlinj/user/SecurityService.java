package fr.lernejo.sqlinj.user;

import fr.lernejo.sqlinj.user.dto.LoginAndPassword;
import fr.lernejo.sqlinj.user.exception.InvalidAuthorizationHeader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
class SecurityService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    private static final Pattern BASIC_AUTH_PATTERN = Pattern.compile("(?<login>[^:]+):(?<password>.+)");

    LoginAndPassword extractFromHeader(String authorizationHeader) {
        String loginAndPassword = new String(Base64.getDecoder().decode(authorizationHeader), StandardCharsets.UTF_8);

        Matcher matcher = BASIC_AUTH_PATTERN.matcher(loginAndPassword);

        if (matcher.matches()) {
            return new LoginAndPassword(matcher.group("login"), matcher.group("password"));
        } else {
            throw new InvalidAuthorizationHeader();
        }
    }

    boolean match(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
