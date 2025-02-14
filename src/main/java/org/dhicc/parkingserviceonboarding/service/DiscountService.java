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
        System.out.println("✅ applyDiscount() 호출됨 - couponCode: " + couponCode + ", originalFee: " + originalFee);

        DiscountCoupon coupon = discountCouponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> {
                    System.out.println("🚨 유효하지 않은 쿠폰 코드: " + couponCode);
                    return new IllegalArgumentException("🚨 유효하지 않은 쿠폰 코드입니다.");
                });

        System.out.println("✅ 쿠폰 조회 성공 - 할인율: " + coupon.getDiscountRate());

        int discountAmount = (originalFee * coupon.getDiscountRate()) / 100;
        int finalFee = Math.max(originalFee - discountAmount, 0);

        System.out.println("✅ 할인 적용 완료 - 최종 금액: " + finalFee);

        return finalFee;
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
