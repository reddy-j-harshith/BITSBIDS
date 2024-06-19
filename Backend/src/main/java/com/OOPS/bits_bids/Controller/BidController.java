package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.ProductDTO;
import com.OOPS.bits_bids.Entity.*;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.ProductRepository;
import com.OOPS.bits_bids.Repository.UserBidRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
public class BidController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final UserBidRepository userBidRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProduct(@RequestBody ProductDTO productDTO){
        User seller = userRepository.findByBitsId(productDTO.getSellerBitsId()).orElseThrow(() -> new RuntimeException("Seller not found"));

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setBasePrice(productDTO.getBasePrice());
        product.setSeller(seller);

        Bid bid = new Bid();
        bid.setStartDate(LocalDateTime.now());
        bid.setDuration(productDTO.getBidDuration());
        bid.setIncrements(productDTO.getBidIncrements());
        bid.setProduct(product);

        product.setBid(bid);

        List<Image> images = new ArrayList<>();
        for (MultipartFile file : productDTO.getImages()) {
            Image image = new Image();
            image.setFilePath(file.getOriginalFilename()); // You need to implement the logic for saving the file and getting its path
            image.setProduct(product);
            images.add(image);
        }

        product.setImages(images);

        productRepository.save(product);
        bidRepository.save(bid);

        // Start the timer for the bid
        // You need to implement the logic for starting the timer and handling the end of the bid

        return ResponseEntity.status(HttpStatus.CREATED).body("Product uploaded successfully");
    }

    // In BidController.java

    @PostMapping("/confirm/{bidId}/{userId}")
    public ResponseEntity<?> confirmBid(@PathVariable Long bidId, @PathVariable String userId) {
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new RuntimeException("Bid not found"));
        User user = userRepository.findByBitsId(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UserBid highestUserBid = userBidRepository.findTopByBidOrderByBidTimeDesc(bid);

        if (highestUserBid != null && highestUserBid.getUser().equals(user)) {
            user.setCredits(user.getCredits() - highestUserBid.getBidAmount());
            userRepository.save(user);

            // Set the bid's active status to false as the bid has ended
            bid.setActiveStatus(false);
            bidRepository.save(bid);

            return ResponseEntity.ok("Bid confirmed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the highest bidder for this bid");
        }
    }

    @Scheduled(cron = "0 * * * * ?") // Runs every minute
    public void checkBidEndTime() {
        List<Bid> bids = bidRepository.findAllActiveBidsWhereCurrentTimeIsAfterEndTime();

        for (Bid bid : bids) {
            UserBid highestUserBid = userBidRepository.findTopByBidOrderByBidTimeDesc(bid);

            if (highestUserBid != null) {
                User user = highestUserBid.getUser();

                // Send confirmation message to the user
                // This is a placeholder, replace with your actual messaging logic
                System.out.println("Confirmation message sent to user: " + user.getBitsId());

                // If the user accepts, deduct the bid amount from their credits and update the user in the database
                // This is a placeholder, replace with your actual acceptance checking logic
                boolean userAccepted = true;

                if (userAccepted) {
                    user.setCredits(user.getCredits() - highestUserBid.getBidAmount());
                    userRepository.save(user);

                    // Set the bid's active status to false as the bid has ended
                    bid.setActiveStatus(false);
                    bidRepository.save(bid);
                }
            }
        }
    }
}