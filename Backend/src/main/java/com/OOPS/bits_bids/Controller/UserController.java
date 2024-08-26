package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Product;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Entity.UserBid;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import com.OOPS.bits_bids.Response.ProductResponse;
import com.OOPS.bits_bids.Response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final BidRepository bidRepository;

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
    public List<Bid> getWishList(@PathVariable String bitsId){
        User user = userRepository.
                findByBitsId(bitsId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        return user.getWishList();
    }

    @PostMapping("/wishlist/{bitsId}/{bidId}")
    public void addToWishList(@PathVariable Long bidId,
                              @PathVariable String bitsId) throws Exception {
        User user = userRepository.
                findByBitsId(bitsId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Please try again"));

        if(user.equals(bidRepository.findByBidId(bidId).orElseThrow(() -> new Exception("Bid is not found!")).getProduct().getSeller()))
            throw new RuntimeException("You cannot add your own product to wishlist");

        Bid bid = bidRepository.findByBidId(bidId).orElseThrow(() -> new RuntimeException("Bid not found"));

        user.getWishList().add(bid);
        bid.getUsersWishList().add(user);

        bidRepository.save(bid);
        userRepository.save(user);
    }

    @GetMapping("/get-participated/{bitsId}")
    public List<ProductResponse> getParticipatedBids(@PathVariable String bitsId){

        User user = userRepository.findByBitsId(bitsId).orElseThrow(() -> new RuntimeException("User not found"));
        List<ProductResponse> list = new ArrayList<>();
        for(UserBid userBid : user.getUserBids()){
            Product product = userBid.getBid().getProduct();
            ProductResponse productResponse = new ProductResponse();
            productResponse.setPId(product.getPid());
            productResponse.setPName(product.getName());
            productResponse.setHighestBid(userBid.getBid().getHighestBid());
            productResponse.setPDesc(product.getDescription());
            productResponse.setBasePrice(product.getBasePrice());
            list.add(productResponse);
        }

        return list;
    }
}
