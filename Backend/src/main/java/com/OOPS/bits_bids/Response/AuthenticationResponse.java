package com.OOPS.bits_bids.Response;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private final String jwt;
    private final String refreshToken;
}
