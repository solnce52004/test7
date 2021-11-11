package dev.example.test7.security.enums;

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
