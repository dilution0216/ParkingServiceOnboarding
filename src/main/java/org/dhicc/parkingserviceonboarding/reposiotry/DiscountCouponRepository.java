package org.dhicc.parkingserviceonboarding.reposiotry;

import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Long> {
    DiscountCoupon findByCouponCode(String couponCode);
}