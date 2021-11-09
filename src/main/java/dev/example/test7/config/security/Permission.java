package dev.example.test7.config.security;

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
