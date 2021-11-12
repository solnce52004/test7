package dev.example.test7.entity;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "users", "permissions"})
@ToString(exclude = {"users", "permissions"})

@Immutable

public class Permission implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToMany(
            fetch = FetchType.LAZY,
            mappedBy = "permissions",
            cascade = CascadeType.PERSIST
    )
    @Fetch(value = FetchMode.JOIN)
    private Set<Role> permissions;
}
