package dev.example.config.security.service;

public interface AuthenticationService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
}
