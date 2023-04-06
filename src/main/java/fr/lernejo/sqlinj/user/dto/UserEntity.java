package fr.lernejo.sqlinj.user.dto;

public record UserEntity(String login, String encodedPassword, String firstName, String lastName) {
}
