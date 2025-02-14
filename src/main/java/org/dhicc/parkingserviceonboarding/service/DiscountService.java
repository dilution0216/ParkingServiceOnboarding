package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountCouponRepository discountCouponRepository;

    public int applyDiscount(String couponCode, int originalFee) {
        DiscountCoupon coupon = discountCouponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 쿠폰 코드입니다."));

        int discountAmount = (originalFee * coupon.getDiscountRate()) / 100;
        return Math.max(originalFee - discountAmount, 0);
    }

    public DiscountCoupon createCoupon(DiscountCoupon coupon) {
        if (discountCouponRepository.findByCouponCode(coupon.getCouponCode()) != null) {
            throw new IllegalArgumentException("이미 존재하는 쿠폰 코드입니다.");
        }
        return discountCouponRepository.save(coupon);
    }

    public List<DiscountCoupon> getAllCoupons() {
        return discountCouponRepository.findAll();
    }

}
