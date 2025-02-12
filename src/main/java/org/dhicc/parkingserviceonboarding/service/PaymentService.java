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

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DiscountCouponRepository discountCouponRepository;

    public Payment processPayment(String vehicleNumber) {
        // 출차 기록이 있는지 확인
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

        // ✅ 쿠폰 조회 (해당 차량 번호에 적용 가능한 쿠폰 확인)
        DiscountCoupon coupon = discountCouponRepository.findByCouponCode(vehicleNumber);

        if (coupon != null) {  // 쿠폰이 존재하는 경우
            int discountAmount = (record.getFee() * coupon.getDiscountRate()) / 100;  // 할인율 적용
            finalFee = Math.max(record.getFee() - discountAmount, 0);  // 최소 요금 0원 보장
            discountDetails = "쿠폰 할인 적용: " + coupon.getCouponCode();
        }

        // 결제 처리
        Payment payment = new Payment();
        payment.setVehicleNumber(vehicleNumber);
        payment.setAmount(finalFee);  // 할인된 요금 적용
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
