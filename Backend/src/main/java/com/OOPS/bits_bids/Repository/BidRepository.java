package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findByBidId(Long bidId);
    @Query("SELECT b FROM Bid b WHERE b.activeStatus = true AND CURRENT_TIMESTAMP > (b.startDate + b.duration * 60 * 60 * 1000)")
    List<Bid> findAllActiveBidsWhereCurrentTimeIsAfterEndTime();
}