package com.OOPS.bits_bids.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductDTO {

    private String name;
    private String description;
    private double basePrice;
    private List<MultipartFile> images;
    private int bidDuration; // Duration in hours
    private int bidIncrements;
}