package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findByBidId(Long bidId);
}