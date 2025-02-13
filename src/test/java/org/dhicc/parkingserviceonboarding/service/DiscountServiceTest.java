package org.dhicc.parkingserviceonboarding.service;


import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountCouponRepository discountCouponRepository;

    @InjectMocks
    private DiscountService discountService;

    @Test
    void testApplyDiscountWithValidCoupon() {
        String couponCode = "DISCOUNT20";
        int originalFee = 10000;

        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCouponCode(couponCode);
        coupon.setDiscountRate(20);

        when(discountCouponRepository.findByCouponCode(couponCode)).thenReturn(coupon);

        int discountedFee = discountService.applyDiscount(couponCode, originalFee);

        assertEquals(8000, discountedFee); // 20% 할인 적용 확인
    }

    @Test
    void testApplyDiscountWithInvalidCoupon() {
        when(discountCouponRepository.findByCouponCode("INVALID")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                discountService.applyDiscount("INVALID", 10000));

        assertEquals("유효하지 않은 쿠폰 코드입니다.", exception.getMessage());
    }
}

