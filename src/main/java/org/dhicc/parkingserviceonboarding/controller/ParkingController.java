package org.dhicc.parkingserviceonboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "주차 기록 API", description = "차량의 입출차 및 요금 계산 관련 API")
public class ParkingController {
    private final ParkingService parkingService;

    @Operation(summary = "입차 기록 등록", description = "차량이 주차장에 입차할 때 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "입차 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/entry/{vehicleNumber}")
    public ResponseEntity<ParkingRecord> registerEntry(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(parkingService.registerEntry(vehicleNumber));
    }

    @Operation(summary = "출차 기록 등록", description = "차량이 주차장에서 출차할 때 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출차 성공"),
            @ApiResponse(responseCode = "400", description = "입차 기록 없음")
    })
    @PostMapping("/exit/{vehicleNumber}")
    public ResponseEntity<ParkingRecord> registerExit(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(parkingService.registerExit(vehicleNumber));
    }

    @Operation(summary = "주차 기록 조회", description = "차량 번호로 입출차 기록을 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{vehicleNumber}")
    public ResponseEntity<List<ParkingRecordDTO>> getParkingRecords(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(parkingService.getParkingRecords(vehicleNumber));
    }
}