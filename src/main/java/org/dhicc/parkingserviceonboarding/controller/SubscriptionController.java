package org.dhicc.parkingserviceonboarding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.SubscriptionDTO;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
@Tag(name = "정기권 API", description = "정기권 등록 및 관리 API")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @Operation(summary = "정기권 등록", description = "차량 번호와 기간을 입력해 정기권을 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정기권 등록 성공"),
            @ApiResponse(responseCode = "400", description = "이미 등록된 차량")
    })
    @PostMapping("/register")
    public ResponseEntity<Subscription> registerSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        Subscription subscription = subscriptionService.registerSubscription(
                subscriptionDTO.getVehicleNumber(),
                subscriptionDTO.getStartDate(),
                subscriptionDTO.getEndDate()
        );
        return ResponseEntity.ok(subscription);
    }

    @Operation(summary = "정기권 취소", description = "차량 번호를 이용해 정기권을 취소")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정기권 취소 성공"),
            @ApiResponse(responseCode = "400", description = "정기권이 존재하지 않음")
    })
    @DeleteMapping("/cancel/{vehicleNumber}")
    public ResponseEntity<String> cancelSubscription(@PathVariable String vehicleNumber) {
        subscriptionService.cancelSubscription(vehicleNumber);
        return ResponseEntity.ok("정기권이 취소되었습니다.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", ex.getMessage()));
    }
}


