package com.OOPS.bits_bids.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    private long credits = 10000;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBid> userBids = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "bid_id"))
    private List<Bid> wishList = new ArrayList<>();

    private String roles;
}
