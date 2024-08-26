package com.OOPS.bits_bids.Response;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {

    private Long pId;
    private String pName;
    private String pDesc;
    private Double basePrice;
    private Long highestBid;
    private Integer bidDuration;
    private Integer bidIncrements;
    private List<String> imageUrls;
}
