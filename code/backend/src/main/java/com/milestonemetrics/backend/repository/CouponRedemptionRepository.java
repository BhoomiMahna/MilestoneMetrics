package com.milestonemetrics.backend.repository;

import com.milestonemetrics.backend.entity.CouponRedemption;
import com.milestonemetrics.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    List<CouponRedemption> findByUserOrderByRedeemedAtDesc(User user);

    Optional<CouponRedemption> findByUserAndCouponId(User user, Long couponId);

    boolean existsByUserAndCouponId(User user, Long couponId);
}
