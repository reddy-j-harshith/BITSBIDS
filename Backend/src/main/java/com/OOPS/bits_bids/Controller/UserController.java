package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Product;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.UserRepository;
import com.OOPS.bits_bids.Response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile/{bitsId}")
    public ProfileResponse UserProfile(@PathVariable String bitsId){
        User user = userRepository.
                findByBitsId(bitsId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        ProfileResponse profile = new ProfileResponse();
        profile.setBitsId(user.getBitsId());
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setHostel(user.getHostel());
        profile.setCredits(user.getCredits());

        return profile;
    }

    @GetMapping("/wishlist/{bitsId}")
    public List<Product> getWishList(@PathVariable String bitsId){
        User user = userRepository.
                findByBitsId(bitsId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

         return user.getWishList();
    }

    @GetMapping("/your-bids/{bitsId}")
    public List<Bid> getActiveBids(@PathVariable String bitsId){
        User user = userRepository.
                findByBitsId(bitsId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        return user.getParticipatedBids();
    }

    @PostMapping("/add-to-wishlist/{bid_id}")
    public ResponseEntity<?> addToWishList(@PathVariable Long bid_id){

        return ResponseEntity.status(HttpStatus.CREATED).body("The item has been added to wishlist!");
    }

}
