package cs204.project.model.player;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(unique = true, nullable = false)
    protected String username;
    protected String password;
    // private Collection<? extends GrantedAuthority> authorities;
    protected String role = "ROLE_USER";
    // public enum userRole {
    //     ADMIN,
    //     USER
    // }
    // private userRole role;

    // Default constructor
    public Player() {
    }

    public Player(Player player) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.password = player.getPassword();
        this.role = player.getRole();
        // this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + player.getRole().toString()));
        // this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + player.getRole()));
    }

    // Constructor with parameters
    // public Player(String username, String password) {
    //     this.username = username;
    //     this.password = password;
    // }

    // public Player(String username, String password, userRole role) {
    //     this.username = username;
    //     this.password = password;
    //     this.role = role;
    //     this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    // }
    public Player(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        // this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // public Player(Long id, String username, String password) {
    //     this.id = id;
    //     this.username = username;
    //     this.password = password;
    // }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // public userRole getRole() {
    //     return role;
    // }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // public void setRole(userRole role) {
    //     this.role = role;
    // }

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     return authorities;
    // }
}
