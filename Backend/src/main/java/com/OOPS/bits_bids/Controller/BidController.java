package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.MakeBidDTO;
import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Entity.UserBid;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.UserBidRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
public class BidController {

    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final UserBidRepository userBidRepository;

    @PostMapping("/make-bid")
    public ResponseEntity<?> makeBid(@RequestBody MakeBidDTO makeBidDTO){
        User user = userRepository.findByBitsId(makeBidDTO.getBitsId()).orElseThrow(() -> new RuntimeException("User not found"));
        Bid bid = bidRepository.findByBidId(makeBidDTO.getBidId()).orElseThrow(() -> new RuntimeException("Bid not found"));

        UserBid userBid = new UserBid();
        UserBid.UserBidId userBidId = new UserBid.UserBidId();
        userBidId.setUserId(makeBidDTO.getBitsId());
        userBidId.setBidId(makeBidDTO.getBidId());
        userBid.setId(userBidId);
        userBid.setUser(user);
        userBid.setBid(bid);
        userBid.setBidAmount(makeBidDTO.getBidAmount());
        userBid.setBidTime(LocalDateTime.now());

        user.getUserBids().add(userBid);
        bid.getUserBids().add(userBid);

        userRepository.save(user);
        bidRepository.save(bid);
        userBidRepository.save(userBid);

        return ResponseEntity.status(HttpStatus.CREATED).body("You have successfully made the bid");
    }

}
