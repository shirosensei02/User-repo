package cs204.project.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cs204.project.Entity.CustomUserDetails;
import cs204.project.Entity.User;
import cs204.project.Repo.UserRepository;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Constructor injection
    public UserDetailService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // return org.springframework.security.core.userdetails.User.builder()
        //         .username(user.getUsername())
        //         .password(user.getPassword())
        //         .roles(user.getRole().split(","))
        //         .build();

        return new CustomUserDetails(user);
    }

    public void registerUser(User user) {
        if (user == null) {
            throw new RuntimeException("Invalid user name or password");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setRank(0);
        repository.save(user);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public User findById(Long id){
      Optional<User> user = repository.findById(id);
      return user.isPresent() ? user.get() : null;

    }
}