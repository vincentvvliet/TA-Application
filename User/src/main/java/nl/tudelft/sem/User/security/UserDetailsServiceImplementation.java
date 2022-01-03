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

    /**
     * Loads a user by given username.
     *
     * @param username String
     * @return UserDetails
     * @throws UsernameNotFoundException if username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails result;
        System.out.println("test2");
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("username is empty");
        }

        Optional<User> foundUser = userRepository.findByUsername(username);
        System.out.println(foundUser); //TODO does not print
        if (foundUser.isPresent()) {
            System.out.println(foundUser.get().getAuthorities());
            result = foundUser.get();
        } else {
            throw new UsernameNotFoundException(username + " is not found");
        }
        return result;
    }
}

