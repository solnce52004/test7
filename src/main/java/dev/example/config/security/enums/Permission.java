package dev.example.config.security.enums;

public enum Permission {
    READER("reader"),
    WRITER("writer");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
