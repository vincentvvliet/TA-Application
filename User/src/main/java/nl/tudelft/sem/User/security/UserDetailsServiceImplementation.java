package nl.tudelft.sem.User.security;

import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
//    private UserDetailsMapper

    /**
     * Loads a user by given username.
     *
     * @param username String
     * @return UserDetails
     * @throws UsernameNotFoundException if username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        System.out.println(user); //TODO does not print
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username + " is not found");
        }
        User foundUser = user.get();
        return new org.springframework.security.core.userdetails.User(foundUser.getUsername(), foundUser.getPassword(), foundUser.getAuthorities());
    }
}

