package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Bid;
import com.OOPS.bits_bids.Entity.Message;
import com.OOPS.bits_bids.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByLenderAndBorrowerAndBid(User lender, User borrower, Bid bid_id);
}
