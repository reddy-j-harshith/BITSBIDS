package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.UserBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBidRepository extends JpaRepository<UserBid, UserBid.UserBidId> {
    UserBid findTopByBidOrderByBidTimeDesc(Bid bid);
}
