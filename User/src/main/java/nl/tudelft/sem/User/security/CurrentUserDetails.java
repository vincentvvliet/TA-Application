package nl.tudelft.sem.User.security;

import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CurrentUserDetails implements UserDetails {
    final String ROLE_PREFIX = "ROLE_";
    private UUID userID;
    private String password;
    private Role role;


    public CurrentUserDetails(UUID userID, String password, Role role) {
        super();
        this.userID = userID;
        this.password = password;
        this.role = role;
    }


  /*    public static UserDetails create(Users entity) {
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    for(Authorities auth: entity.getAuthorities()){
        authorities.add(new SimpleGrantedAuthority(auth.getId().getAuthority()));
    }
    return new MyUserDetail(entity.getUserId(), entity.getLoginId(), entity.getPassword(), entity.getDisplayName(), authorities);
}*/


    public UUID getUserID() {
        return this.userID;
    }


    public Role getRole() {
        return this.role;
    }

    @Override
    public String getPassword() {
        return this.password;
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

//    public static UserDetails create(User entity) {
//        System.out.println(entity.getId() + entity.getPassword() + entity.getRole());
//        return new CurrentUserDetails(entity.getId(), entity.getPassword(), entity.getRole());
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        if (role != null) {
            list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
        }

        return list;
    }

    @Override
    public String getUsername() {
        return null;
    }
}