package dev.example.test7.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "users", "permissions"})
@ToString(exclude = {"users", "permissions"})
@DynamicUpdate
@DynamicInsert

public class Role implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST //не будем удалять роль при удалении юзера
    )
    @JoinTable(
            name = "role_permission",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
    )
    @Fetch(value = FetchMode.JOIN)
//    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions = new HashSet<>();

    public Set<SimpleGrantedAuthority> getPermissionAuthorities() {
        return getPermissions()
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getTitle()))
                .collect(Collectors.toSet());
    }

    public SimpleGrantedAuthority getRoleAuthorities() {
        return new SimpleGrantedAuthority(getTitle());
    }
}
