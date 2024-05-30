package com.OOPS.bits_bids.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long bidId;

    @Column(name = "top_bid")
    private Long highestBid;

    @Column(name = "status")
    private boolean activeStatus;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "time_limit")
    private int duration; // Duration in hours

    @Column(name = "increments")
    private int increments;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserBid> userBids = new ArrayList<>();

    @ManyToMany(mappedBy = "wishList" ,cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> usersWishList = new ArrayList<>();

    @OneToOne
    private Product product;
}
