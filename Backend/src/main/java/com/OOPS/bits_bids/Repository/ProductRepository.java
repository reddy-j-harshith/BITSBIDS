package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findTop9ByOrderByCreatedDateDesc();
}
