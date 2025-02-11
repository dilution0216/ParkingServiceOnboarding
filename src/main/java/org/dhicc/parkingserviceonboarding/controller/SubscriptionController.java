package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.SubscriptionDTO;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/register")
    public ResponseEntity<Subscription> registerSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        Subscription subscription = subscriptionService.registerSubscription(
                subscriptionDTO.getVehicleNumber(),
                subscriptionDTO.getStartDate(),
                subscriptionDTO.getEndDate()
        );
        return ResponseEntity.ok(subscription);
    }

    @DeleteMapping("/cancel/{vehicleNumber}")
    public ResponseEntity<String> cancelSubscription(@PathVariable String vehicleNumber) {
        subscriptionService.cancelSubscription(vehicleNumber);
        return ResponseEntity.ok("정기권이 취소되었습니다.");
    }
}

