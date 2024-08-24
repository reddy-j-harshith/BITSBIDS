package com.OOPS.bits_bids.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreditDTO {

    @NotBlank(message = "BITS ID is mandatory")
    @Pattern(regexp = "f20\\d{6}", message = "Invalid bits_id format")
    private String bitsId;

    @Positive(message = "Credits to be added should be positive")
    private Long addCredits;
}
