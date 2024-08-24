package com.OOPS.bits_bids.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_bid")
public class UserBid {

    @EmbeddedId
    private UserBidId id = new UserBidId();

    @ManyToOne
    @MapsId("userId")
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("bidId")
    private Bid bid;

    @Column(name = "bid_amount")
    private Long bidAmount;

    @Column(name = "bid_time")
    private LocalDateTime bidTime;

    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    @Data
    public static class UserBidId implements Serializable {

        @Column(name = "user_id")
        private String userId;

        @Column(name = "bid_id")
        private Long bidId;

    }
}