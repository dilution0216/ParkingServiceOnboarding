package org.dhicc.parkingserviceonboarding.reposiotry;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {
    ParkingRecord findByVehicleNumberAndExitTimeIsNull(String vehicleNumber);
    ParkingRecord findByVehicleNumberAndExitTimeIsNotNull(String vehicleNumber);
}
