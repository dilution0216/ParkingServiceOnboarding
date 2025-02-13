package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DiscountCouponRepository discountCouponRepository;

    public Payment processPayment(String vehicleNumber, Optional<String> couponCode) {
        // 출차 기록 확인
        ParkingRecord record = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber);
        if (record == null) {
            throw new IllegalArgumentException("해당 차량의 출차 기록이 없습니다.");
        }

        // 정기권 차량인지 확인 (정기권 차량은 결제 X)
        if (subscriptionRepository.findByVehicleNumber(vehicleNumber) != null) {
            return null; // 정기권 차량이면 결제 없이 null 반환
        }

        int finalFee = record.getFee();
        String discountDetails = "기본 요금 적용";

        // 할인 쿠폰 적용
        if (couponCode.isPresent()) {
            DiscountCoupon coupon = discountCouponRepository.findByCouponCode(couponCode.get());
            if (coupon != null) {
                int discountAmount = (finalFee * coupon.getDiscountRate()) / 100;
                finalFee -= discountAmount;
                discountDetails = "쿠폰 할인 적용: " + couponCode.get() + " (" + coupon.getDiscountRate() + "%)";
            }
        }

        // 결제 처리
        Payment payment = new Payment();
        payment.setVehicleNumber(vehicleNumber);
        payment.setAmount(finalFee); // ✅ 할인 적용된 최종 금액 저장
        payment.setDiscountDetails(discountDetails); // ✅ 할인 내역 저장
        payment.setTimestamp(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역을 찾을 수 없습니다."));
    }
}
