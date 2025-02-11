package org.dhicc.parkingserviceonboarding.controller;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process/{vehicleNumber}")
    public ResponseEntity<Payment> processPayment(@PathVariable String vehicleNumber) {
        Payment payment = paymentService.processPayment(vehicleNumber);
        if (payment == null) {
            return ResponseEntity.ok(null); // 정기권 차량은 결제 없음
        }
        return ResponseEntity.ok(payment);
    }
}

