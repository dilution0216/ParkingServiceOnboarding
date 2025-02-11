package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;

    public Payment processPayment(String vehicleNumber) {
        // 출차 기록이 있는지 확인
        ParkingRecord record = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber);
        if (record == null) {
            throw new IllegalArgumentException("해당 차량의 출차 기록이 없습니다.");
        }

        // 정기권 차량인지 확인 (정기권 차량은 결제 필요 없음)
        if (subscriptionRepository.findByVehicleNumber(vehicleNumber) != null) {
            return null; // 정기권 차량이면 결제 없이 null 반환
        }

        // 결제 처리
        Payment payment = new Payment();
        payment.setVehicleNumber(vehicleNumber);
        payment.setAmount(record.getFee());
        payment.setTimestamp(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
