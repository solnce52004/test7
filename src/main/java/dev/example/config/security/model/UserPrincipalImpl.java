package dev.example.config.security.model;

import dev.example.config.security.enums.RoleEnum;
import dev.example.test7.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class UserPrincipalImpl implements OAuth2User, UserDetails {

    private Long id;
    private String email;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isActive = true;
    private Map<String, Object> attributes;

    public UserPrincipalImpl(
            String email,
            String username,
            String password,
            List<SimpleGrantedAuthority> authorities,
            boolean isActive
    ) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }


    public UserPrincipalImpl(
            Long id,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipalImpl create(User user) {
        List<SimpleGrantedAuthority> authorities = user.getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(RoleEnum.ROLE_USER.name())
            );
        }

        return new UserPrincipalImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public static UserPrincipalImpl create(
            User user,
            Map<String, Object> attributes
    ) {
        UserPrincipalImpl userPrincipal = UserPrincipalImpl.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDetails fromUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
//                user.isActive(),
//                user.isActive(),
//                user.isActive(),
//                user.isActive(),
                user.getAuthorities()
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return email;
    }
}
