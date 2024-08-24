package com.OOPS.bits_bids.DTO;

import lombok.Data;

@Data
public class ProductDTO {

    private String name;
    private String description;
    private double basePrice;
    private int bidDuration; // Duration in hours
    private int bidIncrements;
}