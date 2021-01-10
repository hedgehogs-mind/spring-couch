package com.hedgehogsmind.springcouchrest.integration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TestSingleUserDetailsService
        implements UserDetailsService {

    public static class TestUserDetails implements UserDetails {

        public final String username = "tester007";

        public final String password = "TestsAreAwesome1234!";

        public final String encodedPassword;

        public Set<String> authorities = new HashSet<>();

        public boolean accountNonExpired = true;

        public boolean accountNonLocked = true;

        public boolean credentialsNonExpired = true;

        public boolean enabled = true;

        public TestUserDetails(PasswordEncoder passwordEncoder) {
            this.encodedPassword = passwordEncoder.encode(password);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities.stream()
                    .map(a -> new SimpleGrantedAuthority(a))
                    .collect(Collectors.toList());
        }

        @Override
        public String getPassword() {
            return encodedPassword;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return accountNonExpired;
        }

        @Override
        public boolean isAccountNonLocked() {
            return accountNonLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return credentialsNonExpired;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }
    }

    public TestUserDetails testUser;

    private final PasswordEncoder passwordEncoder;

    public TestSingleUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.resetTestUser();
    }

    public void resetTestUser() {
        this.testUser = new TestUserDetails(passwordEncoder);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ( username.equals(this.testUser.username) ) {
            return this.testUser;
        }

        throw new UsernameNotFoundException("user not found: "+username);
    }
}
