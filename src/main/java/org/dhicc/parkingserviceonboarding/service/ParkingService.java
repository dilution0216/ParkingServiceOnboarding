package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.ParkingRecordDTO;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingService {
    private final ParkingRecordRepository parkingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;


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
            record.setFee(calculateFee(record.getEntryTime(), record.getExitTime()));
        } else {
            record.setFee(0);
        }

        return parkingRecordRepository.save(record);
    }

    private int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long durationMinutes = java.time.Duration.between(entryTime, exitTime).toMinutes();
        if (durationMinutes <= 30) return 1000;
        int extraTime = (int) Math.ceil((durationMinutes - 30) / 10.0);
        return Math.min(1000 + (extraTime * 500), 15000);
    }
}
