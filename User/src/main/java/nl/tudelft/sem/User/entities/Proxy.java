package nl.tudelft.sem.User.entities;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class Proxy implements User {

    private RealUser user;
    private Role role;
    private String username;
    private String password;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        check();
        return this.user.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        check();
        return this.user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        check();
        return this.user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        check();
        return this.user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        check();
        return this.user.isEnabled();
    }

    @Override
    public UUID getId() {
        check();
        return this.user.getId();
    }

    @Override
    public String getUsername() {
        check();
        return this.user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        check();
        this.user.setUsername(username);
    }

    @Override
    public String getPassword() {
        check();
        return this.user.getPassword();
    }

    @Override
    public void setPassword(String password) {
        check();
        this.user.setPassword(password);
    }

    @Override
    public Role getRole() {
        check();
        return this.user.getRole();
    }

    @Override
    public void setRole(Role role) {
        check();
        this.user.setRole(role);
    }

    /**
     * Check whether a user already exists, if not then create new user.
     *
     * @return RealUser
     */
    private RealUser check() {
        if (user == null) {
            return new RealUser(username, password, role);
        }

        return user;
    }
}
