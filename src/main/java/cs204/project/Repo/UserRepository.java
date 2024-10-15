package cs204.project.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cs204.project.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    <Optional> User findByUsername(String username);
    <Optional> User findByRole(String role);
    // <Optional> User findById(Long id);
    List<User> findAll();
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}

