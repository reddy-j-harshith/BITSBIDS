package com.OOPS.bits_bids.Controller;

import com.OOPS.bits_bids.DTO.ProductDTO;
import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Image;
import com.OOPS.bits_bids.Entity.Product;
import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.BidRepository;
import com.OOPS.bits_bids.Repository.ProductRepository;
import com.OOPS.bits_bids.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class BidController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;

    private final Path rootLocation = Paths.get("uploaded-images");

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProduct(@RequestPart("product") String productJson,
                                           @RequestPart("images") List<MultipartFile> images) {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO;
        try {
            productDTO = objectMapper.readValue(productJson, ProductDTO.class);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format for product");
        }

        User seller = userRepository.findByBitsId(productDTO.getSellerBitsId())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

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

        // Ensure the storage directory exists
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not initialize storage");
        }

        productRepository.save(product);
        bidRepository.save(bid);
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

        // Save bid first


        // Save product


        return ResponseEntity.status(HttpStatus.CREATED).body("Product uploaded successfully");
    }
}