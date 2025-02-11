package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping("/apply/{couponCode}/{fee}")
    public ResponseEntity<Integer> applyDiscount(@PathVariable String couponCode, @PathVariable int fee) {
        int discountedFee = discountService.applyDiscount(couponCode, fee);
        return ResponseEntity.ok(discountedFee);
    }

    // 할인 쿠폰 생성 API
    @PostMapping("/create")
    public ResponseEntity<DiscountCoupon> createCoupon(@RequestBody DiscountCoupon coupon) {
        return ResponseEntity.ok(discountService.createCoupon(coupon));
    }

    // 모든 할인 쿠폰 조회 API
    @GetMapping("/all")
    public ResponseEntity<List<DiscountCoupon>> getAllCoupons() {
        return ResponseEntity.ok(discountService.getAllCoupons());
    }
}
