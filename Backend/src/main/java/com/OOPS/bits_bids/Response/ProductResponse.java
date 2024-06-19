package com.OOPS.bits_bids.Response;

import com.OOPS.bits_bids.Entity.Image;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {

    private Long pId;

    private String pName;

    private List<Image> images;

    private Long highest;

    private Long bid;

}
