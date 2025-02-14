package org.dhicc.parkingserviceonboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discount")
@RequiredArgsConstructor
@Tag(name = "할인 쿠폰 API", description = "할인 쿠폰 생성 및 적용")
public class DiscountController {
    private final DiscountService discountService;

    @Operation(summary = "할인 적용", description = "쿠폰 코드를 이용하여 주차 요금에 할인 적용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할인 적용 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 쿠폰 코드")
    })
    @PostMapping("/apply/{couponCode}/{fee}")
    public ResponseEntity<Integer> applyDiscount(
            @PathVariable String couponCode,
            @PathVariable int fee
    ) {
        int discountedFee = discountService.applyDiscount(couponCode, fee);
        return ResponseEntity.ok(discountedFee);
    }

    @Operation(summary = "할인 쿠폰 생성", description = "새로운 할인 쿠폰을 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 생성 완료")
    })
    @PostMapping("/create")
    public ResponseEntity<DiscountCoupon> createCoupon(@RequestBody DiscountCoupon coupon) {
        return ResponseEntity.ok(discountService.createCoupon(coupon));
    }

    @Operation(summary = "할인 쿠폰 목록 조회", description = "모든 할인 쿠폰을 조회")
    @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공")
    @GetMapping("/all")
    public ResponseEntity<List<DiscountCoupon>> getAllCoupons() {
        return ResponseEntity.ok(discountService.getAllCoupons());
    }
}

