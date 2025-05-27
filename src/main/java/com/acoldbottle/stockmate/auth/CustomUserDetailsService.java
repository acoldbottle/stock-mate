package com.acoldbottle.stockmate.auth;

import com.acoldbottle.stockmate.domain.User;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.UserNotFoundException;
import com.acoldbottle.stockmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
