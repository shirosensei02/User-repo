package cs204.project.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cs204.project.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByRole(String username);
    List<User> findAll();
}

