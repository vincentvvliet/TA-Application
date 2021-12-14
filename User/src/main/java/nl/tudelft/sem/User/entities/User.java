package nl.tudelft.sem.User.entities;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public interface User {
    public Collection<GrantedAuthority> getAuthorities();

    public boolean isAccountNonExpired();

    public boolean isAccountNonLocked();

    public boolean isCredentialsNonExpired();

    public boolean isEnabled();

    public UUID getId();

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public Role getRole();

    public void setRole(Role role);
}
