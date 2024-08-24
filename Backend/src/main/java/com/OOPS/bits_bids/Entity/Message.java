package com.OOPS.bits_bids.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue
    private Long mId;

    @ManyToOne
    @JoinColumn(name = "lender_id")
    private User lender;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @Column(name = "message")
    private String content;
}
