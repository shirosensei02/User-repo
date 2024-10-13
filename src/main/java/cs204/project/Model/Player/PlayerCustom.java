package cs204.project.model.player;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Collections;

import cs204.project.model.player.*;

public class PlayerCustom extends Player implements UserDetails{
    private Collection<? extends GrantedAuthority> authorities;

    public PlayerCustom(Player player) {
        this.id = player.getId();
        this.username = player.getUsername();
        this.password = player.getPassword();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + player.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
