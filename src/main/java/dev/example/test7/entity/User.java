package dev.example.test7.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.example.config.security.enums.UserStatusEnum;
import dev.example.test7.converter.UserPasswordAttributeConverter;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "roles"})
@ToString(exclude = {"roles"})
@DynamicUpdate
@DynamicInsert

public class User implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @Convert(converter = UserPasswordAttributeConverter.class)
    private String password;

    transient private String confirmPassword;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST //не будем удалять роль при удалении юзера
    )
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    @Fetch(value = FetchMode.JOIN)
    private Set<Role> roles;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(GenerationTime.ALWAYS) //генерит время +3
    @Column(name="created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name="updated_at", insertable = false, columnDefinition = "TIMESTAMP")
    private Date updatedAt;

    public boolean isActive(){
       return getStatus().equals(UserStatusEnum.ACTIVE);
    }

    /**
     * Смешанный (но уникальный) список ROLES, PERMISSIONS
     * завернутый в SimpleGrantedAuthority
     * сможем делать ограничения по ролям, либо по authority
     */
    @Transactional
    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : getRoles()) {
            authorities.addAll(role.getPermissionAuthorities());
            authorities.add(role.getRoleAuthorities());
        }

        return authorities;
    }
}
