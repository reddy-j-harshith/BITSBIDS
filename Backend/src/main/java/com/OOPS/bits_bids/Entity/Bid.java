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

    @ManyToOne
    private User highestBidder;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "time_limit")
    private int duration; // Duration in hours

    @Column(name = "increments")
    private int increments;

    @OneToMany(mappedBy = "bid", cascade = CascadeType.ALL)
    private List<UserBid> userBids = new ArrayList<>();

    @ManyToMany(mappedBy = "wishList" ,cascade = CascadeType.ALL)
    private List<User> usersWishList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Product product;

    public enum Status {
        OPEN, BIDDERS_ENGAGED, CLOSED, REMOVED
    }

    @Enumerated(EnumType.STRING)
    private Status status;
}
