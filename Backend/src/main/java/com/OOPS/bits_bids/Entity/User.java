package com.OOPS.bits_bids.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user_info")
public class User {

    @Id
    @Column(name = "bits_id")
    @NotBlank(message = "Bits ID is mandatory")
    @Pattern(regexp = "f20\\d{2}[a-zA-Z]{4}", message = "Bits ID format is invalid")
    private String bitsId;

    @Column(name = "bits_mail")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Pattern(regexp = "f20\\d{2}[a-zA-Z]{4}@hyderabad\\.bits-pilani\\.ac\\.in", message = "Email format is invalid")
    private String mail;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "hostel")
    private String hostel;

    private long Balance = 10000;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> wishList = new HashSet<>();

    @ManyToMany(mappedBy = "bidders")
    private Set<Bid> participatedBids = new HashSet<>();

    private String roles;
}
