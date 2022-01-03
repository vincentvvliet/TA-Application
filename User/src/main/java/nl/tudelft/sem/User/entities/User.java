package nl.tudelft.sem.User.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "user", schema = "userschema")
public class User implements UserDetails {
    @Id
    @Column(name = "id", nullable = false)
    @JsonProperty(value = "id")
    private final UUID id;

    @Column(name = "username")
    @JsonProperty(value = "username")
    private String username;

    @Column(name = "password")
    @JsonProperty(value = "password")
    private String password;

    @Column(name = "role")
    @JsonProperty(value = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Instantiates a new user.
     */
    public User() {
        this.id = UUID.randomUUID();
    }

    /**
     * Instantiates a new user.
     *
     * @param username the username
     * @param password the password
     * @param role     the role
     */
    public User(String username, String password, Role role) {
        super();
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username
                + ", password=" + password + ", role=" + role + "]";
    }


    /**
     * Get authorities of a user, with authorities being the specified roles that a user has.
     *
     * @return list of authorities
     */
    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() { //TODO method never called, so roles never assigned
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        if (role != null) {
            list.add(new SimpleGrantedAuthority(role.toString()));
        }

        System.out.println(list);
        return list;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
