package org.dhicc.parkingserviceonboarding.service;


import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ParkingRecordRepository parkingRecordRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private DiscountCouponRepository discountCouponRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testProcessPaymentWithDiscount() {
        String vehicleNumber = "5678CD";
        String couponCode = "DISCOUNT20";

        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber(vehicleNumber);
        record.setExitTime(LocalDateTime.now());
        record.setFee(10000);

        DiscountCoupon mockCoupon = new DiscountCoupon();
        mockCoupon.setCouponCode(couponCode);
        mockCoupon.setDiscountRate(20); // 20% 할인 적용

        when(parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber)).thenReturn(record);
        when(subscriptionRepository.findByVehicleNumber(vehicleNumber)).thenReturn(null);
        when(discountCouponRepository.findByCouponCode(couponCode)).thenReturn(mockCoupon);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0)); // 정상적인 위치에 배치

        // processPayment 호출
        Payment payment = paymentService.processPayment(vehicleNumber, Optional.of(couponCode));

        // 결과 검증
        assertNotNull(payment);
        assertTrue(payment.getAmount() < 10000); // 쿠폰 적용 후 금액이 10000원 미만인지 확인
        assertTrue(payment.getDiscountDetails().contains("쿠폰 할인")); // 쿠폰 적용 여부 확인
    }
}