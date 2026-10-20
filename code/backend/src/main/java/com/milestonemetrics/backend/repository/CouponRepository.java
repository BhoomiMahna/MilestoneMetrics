package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByActiveTrueOrderByPointsRequiredAsc();

    List<Coupon> findAllByOrderByPointsRequiredAsc();

    long countByActiveTrue();
}
