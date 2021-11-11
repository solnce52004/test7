package dev.example.test7.repo;

import dev.example.test7.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from users u where u.name = :name")
    List<User> findAllByName(@Param("name") String name);

    Optional<User> findByEmail(String email);
}
