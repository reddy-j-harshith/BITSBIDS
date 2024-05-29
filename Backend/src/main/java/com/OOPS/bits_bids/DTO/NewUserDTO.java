package com.OOPS.bits_bids.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NewUserDTO {

    @NotBlank(message = "BITS mail is mandatory")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "f20\\d{6}@hyderabad\\.bits-pilani\\.ac\\.in", message = "Invalid bits_mail format")
    private String bitsMail;

    private String password;

    private String firstName;

    private String lastName;

    private String hostel;
}
