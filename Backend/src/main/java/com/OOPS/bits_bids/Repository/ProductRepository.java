package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
