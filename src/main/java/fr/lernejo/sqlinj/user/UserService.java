package fr.lernejo.sqlinj.user;

import fr.lernejo.sqlinj.user.dto.LoginAndPassword;
import fr.lernejo.sqlinj.user.dto.User;
import fr.lernejo.sqlinj.user.dto.UserDetailsForInscription;
import fr.lernejo.sqlinj.user.dto.UserEntity;
import fr.lernejo.sqlinj.user.exception.UnauthorizedUser;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {

    private final SecurityService securityService;
    private final UserRepository userRepository;

    UserService(SecurityService securityService, UserRepository userRepository) {
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    User authenticate(String authorizationHeader) {
        Optional<User> authenticatedUser = getAuthenticatedUser(authorizationHeader);
        return authenticatedUser.orElseThrow(() -> new UnauthorizedUser());
    }

    public boolean isAuthenticated(String authorizationHeader) {
        return getAuthenticatedUser(authorizationHeader).isPresent();
    }

    Optional<User> getAuthenticatedUser(String authorizationHeader) {
        LoginAndPassword loginAndPassword = securityService.extractFromHeader(authorizationHeader);
        Optional<UserEntity> user = userRepository.findUserByLogin(loginAndPassword.login());
        if (user.isPresent() && securityService.match(loginAndPassword.password(), user.get().encodedPassword())) {
            return user.map(this::mapFromEntity);
        } else {
            return Optional.empty();
        }
    }

    private User mapFromEntity(UserEntity userEntity) {
        return new User(
            userEntity.login(),
            userEntity.firstName(),
            userEntity.lastName()
        );
    }

    User inscription(UserDetailsForInscription userDetailsForInscription) {
        UserEntity user = userRepository.createUser(mapToEntity(userDetailsForInscription));
        return mapFromEntity(user);
    }

    private UserEntity mapToEntity(UserDetailsForInscription userDetailsForInscription) {
        return new UserEntity(
            userDetailsForInscription.login(),
            securityService.encodePassword(userDetailsForInscription.password()),
            userDetailsForInscription.firstName(),
            userDetailsForInscription.lastName()
        );
    }

    String obfuscate(String id) {
        return Base64.getEncoder().encodeToString(SerializationUtils.serialize(id));
    }

    public String desobfuscate(String b64payload) {
        return (String) SerializationUtils.deserialize(Base64.getDecoder().decode(b64payload));
    }
}
