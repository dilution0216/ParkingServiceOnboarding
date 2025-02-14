package org.dhicc.parkingserviceonboarding.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
import org.dhicc.parkingserviceonboarding.dto.ParkingRecordDTO;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PricingPolicy pricingPolicy;
    private final DiscountService discountService; // 추가



    public List<ParkingRecordDTO> getParkingRecords(String vehicleNumber) {
        List<ParkingRecord> records = parkingRecordRepository.findByVehicleNumber(vehicleNumber);
        if (records == null || records.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환 (null 방지)
        }
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

        subscriptionRepository.findByVehicleNumber(vehicleNumber)
                .ifPresent(record::setSubscription);

        return parkingRecordRepository.save(record);
    }

    public ParkingRecord registerExit(String vehicleNumber) {
        // 기존 출차 기록이 없으면 새로운 출차 기록 생성 (입차 기록이 없어도 출차 가능)
        ParkingRecord record = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNull(vehicleNumber)
                .orElseGet(() -> {
                    ParkingRecord newRecord = new ParkingRecord();
                    newRecord.setVehicleNumber(vehicleNumber);
                    newRecord.setEntryTime(LocalDateTime.now()); // 현재 시간 기준 가상의 입차 기록 생성
                    return parkingRecordRepository.save(newRecord);
                });

        // 출차 시간 설정 및 요금 계산
        record.setExitTime(LocalDateTime.now());
        if (record.getSubscription() == null) {
            record.setFee(calculateFee(record.getEntryTime(), record.getExitTime(), Optional.empty()));
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
