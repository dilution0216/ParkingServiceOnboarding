package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
import org.dhicc.parkingserviceonboarding.dto.ParkingRecordDTO;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingService {
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PricingPolicy pricingPolicy;
    private final DiscountService discountService; // 추가



    public List<ParkingRecordDTO> getParkingRecords(String vehicleNumber) {
        List<ParkingRecord> records = parkingRecordRepository.findByVehicleNumber(vehicleNumber);
        return records.stream().map(record -> {
            ParkingRecordDTO dto = new ParkingRecordDTO();
            dto.setVehicleNumber(record.getVehicleNumber());
            dto.setEntryTime(record.getEntryTime());
            dto.setExitTime(record.getExitTime());
            dto.setFee(record.getFee());
            return dto;
        }).collect(Collectors.toList());
    }

    public ParkingRecord registerEntry(String vehicleNumber) {
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber(vehicleNumber);
        record.setEntryTime(LocalDateTime.now());

        Subscription subscription = subscriptionRepository.findByVehicleNumber(vehicleNumber);
        record.setSubscription(subscription);

        return parkingRecordRepository.save(record);
    }

    public ParkingRecord registerExit(String vehicleNumber) {
        ParkingRecord record = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNull(vehicleNumber);
        if (record == null) {
            throw new IllegalArgumentException("해당 차량의 입차 기록이 존재하지 않습니다.");
        }

        record.setExitTime(LocalDateTime.now());
        if (record.getSubscription() == null) {
            record.setFee(calculateFee(record.getEntryTime(), record.getExitTime(),Optional.empty()));
        } else {
            record.setFee(0);
        }

        return parkingRecordRepository.save(record);
    }

    public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, Optional<String> couponCode) {
        long durationMinutes = java.time.Duration.between(entryTime, exitTime).toMinutes();
        long durationHours = durationMinutes / 60;

        if (durationMinutes <= 30) return pricingPolicy.getBaseFee();

        int extraTime = (int) Math.ceil((durationMinutes - 30) / 10.0);
        int totalFee = Math.min(
                pricingPolicy.getBaseFee() + (extraTime * pricingPolicy.getExtraFeePer10Min()),
                pricingPolicy.getDailyMaxFee()
        );

        // 야간 할인 적용
        int exitHour = exitTime.getHour();
        if (exitHour >= 23 || exitHour < 7) {
            totalFee *= (1 - pricingPolicy.getNightDiscount());
        }

        // 주말 할인 적용
        int exitDayOfWeek = exitTime.getDayOfWeek().getValue();
        if (exitDayOfWeek == 6 || exitDayOfWeek == 7) {
            totalFee *= (1 - pricingPolicy.getWeekendDiscount());
        }

        // 장기 주차 요금 제한
        int maxFeeForDays = pricingPolicy.getDailyMaxFee() * pricingPolicy.getMaxDaysCharged();
        if (durationHours >= pricingPolicy.getMaxDaysCharged() * 24) {
            totalFee = maxFeeForDays;
        }

        // 할인 쿠폰 적용
        if (couponCode.isPresent()) {
            totalFee = discountService.applyDiscount(couponCode.get(), totalFee);
        }

        return totalFee;
    }
}
