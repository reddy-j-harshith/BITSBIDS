package com.OOPS.bits_bids.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(name = "p_name")
    private String name;

    @Lob
    private String description;

    @Column(name = "base_price")
    private double basePrice;

    @ManyToOne
    @JoinColumn(name = "lender_id")
    private User seller;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "product")
    private Bid bid;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
