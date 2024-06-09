package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.ProductDTO;
import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Image;
import com.OOPS.bits_bids.Entity.Product;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.ProductRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class BidController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

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
}