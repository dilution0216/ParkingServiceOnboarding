package org.dhicc.parkingserviceonboarding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PricingPolicy {
    private int baseFee = 1000; // 최초 30분 요금
    private int extraFeePer10Min = 500; // 추가 10분당 요금
    private int dailyMaxFee = 15000; // 일일 최대 요금
    private int maxDaysCharged = 3; // 최대 부과 일수
    private double nightDiscount = 0.2; // 야간 할인율 (20%)
    private double weekendDiscount = 0.1; // 주말 할인율 (10%)
    private int maxCouponUses = 5; // 최대 쿠폰 사용 횟수
    private int maxDiscountRate = 50; // 쿠폰 최대 할인율 (50%)
}

