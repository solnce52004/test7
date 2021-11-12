package dev.example.test7.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.internal.util.stereotypes.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "roles"})
@ToString(exclude = {"roles"})
@DynamicUpdate
@DynamicInsert

public class Permission implements Serializable {
    private static final Long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Immutable
    @Column(name = "title", nullable = false)
    private String title;

    @ManyToMany(
            fetch = FetchType.EAGER,
            mappedBy = "permissions",
            cascade = CascadeType.PERSIST
    )
    @Fetch(value = FetchMode.JOIN)
    private Set<Role> roles = new HashSet<>();
}
