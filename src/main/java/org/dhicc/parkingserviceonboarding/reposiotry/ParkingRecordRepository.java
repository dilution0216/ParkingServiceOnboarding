package org.dhicc.parkingserviceonboarding.reposiotry;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {
    ParkingRecord findByVehicleNumberAndExitTimeIsNull(String vehicleNumber);
    ParkingRecord findByVehicleNumberAndExitTimeIsNotNull(String vehicleNumber);
    // 차량 번호별 주차 기록 조회
    List<ParkingRecord> findByVehicleNumber(String vehicleNumber);
}
