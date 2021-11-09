package dev.example.test7.config.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    //////////////
    // BY ROLES
//    ADMIN,
//    USER;

    ////////////////////////
    // BY PERMISSIONS
    ADMIN(Set.of(Permission.READER, Permission.WRITER)),
    USER(Set.of(Permission.READER));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions()
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
    }
}
