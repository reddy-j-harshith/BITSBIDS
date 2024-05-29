package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
