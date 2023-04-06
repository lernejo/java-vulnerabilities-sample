package fr.lernejo.sqlinj.user;

import fr.lernejo.sqlinj.user.dto.User;
import fr.lernejo.sqlinj.user.dto.UserDetailsForInscription;
import org.springframework.web.bind.annotation.*;

@RestController
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/login")
    User authenticate(@RequestHeader("Authorization") String authorizationHeader) {
        return userService.authenticate(authorizationHeader);
    }

    @PostMapping("/api/registration")
    User registration(@RequestBody UserDetailsForInscription userDetailsForInscription) {
        return userService.inscription(userDetailsForInscription);
    }
}
