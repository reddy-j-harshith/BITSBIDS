package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
