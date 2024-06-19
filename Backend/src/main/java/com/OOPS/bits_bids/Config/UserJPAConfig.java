package com.OOPS.bits_bids.Config;

import com.OOPS.bits_bids.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserJPAConfig implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String bitsId) throws UsernameNotFoundException {
        return userRepository
                .findById(bitsId)
                .map(UserConfig::new)
                .orElseThrow(() -> new UsernameNotFoundException("Please login / sign-up with your bits mail."));
    }
}
