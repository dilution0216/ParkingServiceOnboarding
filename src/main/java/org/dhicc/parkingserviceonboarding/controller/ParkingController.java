package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.ParkingRecordDTO;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/entry/{vehicleNumber}")
    public ResponseEntity<ParkingRecord> registerEntry(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(parkingService.registerEntry(vehicleNumber));
    }

    @PostMapping("/exit/{vehicleNumber}")
    public ResponseEntity<ParkingRecord> registerExit(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(parkingService.registerExit(vehicleNumber));
    }

    // 서비스 호출하여 차량 번호별 주차 기록을 조회 후 반환
    @GetMapping("/records/{vehicleNumber}")
    public ResponseEntity<List<ParkingRecordDTO>> getParkingRecords(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(parkingService.getParkingRecords(vehicleNumber));
    }
}

