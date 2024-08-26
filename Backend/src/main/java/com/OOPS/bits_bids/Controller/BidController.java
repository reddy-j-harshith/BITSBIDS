package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.ProductDTO;
import com.OOPS.bits_bids.Entity.*;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.ProductRepository;
import com.OOPS.bits_bids.Repository.UserBidRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import com.OOPS.bits_bids.Service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class BidController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final UserBidRepository userBidRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final Path rootLocation = Paths.get("uploaded-images");

    @PostMapping(value = "/upload/{bitsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProduct(@RequestPart("product") String productJson,
                                           @RequestPart("images") List<MultipartFile> images,
                                           @PathVariable String bitsId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO;
        try {
            productDTO = objectMapper.readValue(productJson, ProductDTO.class);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format for product");
        }

        User seller = userRepository.findByBitsId(bitsId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (productDTO.getBasePrice() <= 0)
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Base price cannot be negative!");

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
        bid.setStatus(Bid.Status.OPEN);
        product.setBid(bid);

        // Ensure the storage directory exists
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not initialize storage");
        }

        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : images) {
            try {
                String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
                Path destinationFile = rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

                if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot store file outside current directory.");
                }

                try (var inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationFile);
                }

                Image image = new Image();
                image.setFilePath(filename);
                image.setProduct(product);
                imageList.add(image);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store file.");
            }
        }

        product.setImages(imageList);
        productRepository.save(product); // This will also save the bid due to cascading.
        long delay = productDTO.getBidDuration();
        scheduler.schedule(() -> endBid(bid.getBidId()), delay, TimeUnit.SECONDS);

        mailService.sendMail(seller.getMail(), "Product uploaded successfully", "Your product has been uploaded with ID: " + product.getPid());

        return ResponseEntity.status(HttpStatus.CREATED).body("Product uploaded successfully. Bid ID: " + bid.getBidId());
    }

    void endBid(Long bidId) {
        Bid latestBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        if (latestBid.getHighestBidder() != null) {
            User seller = latestBid.getProduct().getSeller();
            seller.setCredits(seller.getCredits() + latestBid.getHighestBid());
            userRepository.save(seller);
            mailService.sendMail(latestBid.getHighestBidder().getMail(), "You've won the bid", "Congratulations! You've won the bid for the product.");
            mailService.sendMail(seller.getMail(), "Your product has been sold", "Your product has been sold to " + latestBid.getHighestBidder().getBitsId() + " for " + latestBid.getHighestBid() + " credits.");
        }
        latestBid.setStatus(Bid.Status.CLOSED);
        bidRepository.save(latestBid);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProducts() {
        productRepository.deleteAll();
        return ResponseEntity.ok("All products deleted successfully");
    }

    // Bidding Logic
    @PostMapping("/bid/{bitsId}/{bidId}/{bidAmount}")
    public ResponseEntity<?> placeBid(@PathVariable Long bidId,
                                      @PathVariable Long bidAmount,
                                      @RequestParam String password,
                                      @PathVariable String bitsId) {

        User user = userRepository.findByBitsId(bitsId).orElseThrow(() -> new RuntimeException("User not found"));
        Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new RuntimeException("Bid not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password, Try again!");
        } else if (bid.getStatus() == Bid.Status.CLOSED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid is closed");
        } else if (bid.getStatus() == Bid.Status.REMOVED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid has been removed");
        } else if (bitsId.equalsIgnoreCase(bid.getProduct().getSeller().getBitsId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seller cannot bid on their own product");
        } else if (bidAmount < bid.getProduct().getBasePrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid amount should be greater than the base price");
        } else if (bid.getHighestBid() != null && bid.getHighestBid() >= bidAmount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid amount should be greater than the highest bid");
        } else if ((bidAmount - (bid.getHighestBid() == null ? bid.getProduct().getBasePrice() : bid.getHighestBid())) % bid.getIncrements() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bid amount should be in multiples of increments");
        } else if (user.getCredits() < bidAmount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient credits");
        }

        if (bid.getStatus() == Bid.Status.OPEN) {
            bid.setStatus(Bid.Status.BIDDERS_ENGAGED);
        }

        UserBid.UserBidId id = new UserBid.UserBidId();
        id.setUserId(user.getBitsId());
        id.setBidId(bidId);
        Optional<UserBid> existingUserBid = userBidRepository.findById(id);

        if (bid.getHighestBid() != null && bid.getHighestBidder() != null) {
            bid.getHighestBidder().setCredits(bid.getHighestBidder().getCredits() + bid.getHighestBid());
            mailService.sendMail(bid.getHighestBidder().getMail(), "You've been outbid", "Someone has placed a higher bid on the product " + bid.getProduct().getName() + ". Your credits have been refunded.");
        }

        user.setCredits(user.getCredits() - bidAmount);

        UserBid userBid;
        if (existingUserBid.isPresent()) {
            userBid = existingUserBid.get();
        } else {
            userBid = new UserBid();
            userBid.setId(id);
            userBid.setUser(user);
            userBid.setBid(bid);
        }
        userBid.setBidAmount(bidAmount);
        userBid.setBidTime(LocalDateTime.now());

        userBidRepository.save(userBid);

        bid.setHighestBidder(user);
        bid.setHighestBid(bidAmount);

        bidRepository.save(bid);
        userRepository.save(user);

        return ResponseEntity.ok("Bid placed successfully");
    }

    @PostConstruct
    public void initScheduledTasks() {
        List<Bid> allBids = bidRepository.findAll();
        for (Bid bid : allBids) {
            if (bid.getStatus() == Bid.Status.BIDDERS_ENGAGED) {
                rollbackBid(bid);
            } else if (bid.getStatus() == Bid.Status.OPEN) {
                bid.setStatus(Bid.Status.REMOVED);
                bidRepository.save(bid);
            }
        }
    }

    void rollbackBid(Bid bid) {
        long remainingTime = bid.getStartDate().plusSeconds(bid.getDuration()).minusSeconds(LocalDateTime.now().getSecond()).getSecond();
        scheduler.schedule(() -> endBid(bid.getBidId()), remainingTime, TimeUnit.SECONDS);
    }

}
