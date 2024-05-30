package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Product;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.UserRepository;
import com.OOPS.bits_bids.Response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile/{id}")
    public ProfileResponse UserProfile(@PathVariable String id){
        User user = userRepository.
                findByBitsId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        ProfileResponse profile = new ProfileResponse();
        profile.setBitsId(user.getBitsId());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setHostel(user.getHostel());
        profile.setCredits(user.getCredits());

        return profile;
    }

    @GetMapping("user/wishlist/{id}")
    public List<Product> getWishList(@PathVariable String id){
        User user = userRepository.
                findByBitsId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

         return user.getWishList();
    }

    @GetMapping("/user/your-bids/{id}")
    public List<Bid> getActiveBids(@PathVariable String id){
        User user = userRepository.
                findByBitsId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        return user.getParticipatedBids();
    }
}
