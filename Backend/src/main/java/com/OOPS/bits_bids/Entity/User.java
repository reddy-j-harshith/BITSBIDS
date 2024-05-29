package com.OOPS.bits_bids.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user_info")
public class User {

    @Id
    @Column(name = "bits_id")
    private String bitsId;

    @Column(name = "bits_mail")
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
