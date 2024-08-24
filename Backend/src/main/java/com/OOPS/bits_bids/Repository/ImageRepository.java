package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
