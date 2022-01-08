package nl.tudelft.sem.User.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.util.Objects.requireNonNull;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImplementation userDetailsService;

    /**
     * The Token Provider.
     */
    TokenAuthenticationProvider provider;

    /**
     * Instantiates a new Security config.
     *
     * @param provider the token provider
     */
    SecurityConfig(TokenAuthenticationProvider provider) {
        super();
        this.provider = requireNonNull(provider);
    }

    /**
     * Configure the authentication provider.
     *
     * @param auth the authentication manager builder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }

    /**
     * Configure the http security, with required roles for specified endpoints.
     *
     * @param http HttpSecurity object
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeRequests()
                .antMatchers("/user/login", "/user/register")
                .permitAll()
                .antMatchers("/user/getUsers").hasAuthority("ADMIN") // TODO .permitAll() works, role doesn't
                .antMatchers("/user/getUser").hasAuthority("ADMIN") // TODO always works
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }

    /**
     * Configure the userdetails of the authentication.
     *
     * @param auth the auth
     * @throws Exception
     */
    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setPasswordEncoder(passwordEncoder());
        daoProvider.setUserDetailsService(this.userDetailsService);
        return daoProvider;
    }

    /**
     * Password encoder to encode password using BCrypt.
     *
     * @return the password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
