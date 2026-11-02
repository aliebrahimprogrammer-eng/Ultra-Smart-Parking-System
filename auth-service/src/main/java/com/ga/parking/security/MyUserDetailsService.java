package com.ga.parking.security;

import com.ga.parking.model.User;
import com.ga.parking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//    Security Packages --  Classes
//    Our Classes         -> Implements ->   Spring Security Interface
//    MyUserDetailsService                   UserDetailsService
//    MyUserDetails                          UserDetails
//    SecurityConfiguration

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmailAddress(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetails(user);
    }
}
