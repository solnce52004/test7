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

    @Query("select u from users u where u.username = :username")
    List<User> findAllByName(@Param("username") String username);

    Optional<User> findByEmail(String email);

    @Query("select u.email from users u")
    List<String> getAllEmails();
}
