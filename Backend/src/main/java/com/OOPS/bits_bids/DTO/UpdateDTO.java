package com.OOPS.bits_bids.DTO;

import lombok.Data;

@Data
public class UpdateDTO {
    private String bitsId;
    private String oldPassword;
    private String newPassword;
}
