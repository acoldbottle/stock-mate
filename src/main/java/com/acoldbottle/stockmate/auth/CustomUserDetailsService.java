package com.acoldbottle.stockmate.auth;

import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        log.error("asdasdasd {}", user.getUsername());
        return new CustomUserDetails(user);
    }
}
