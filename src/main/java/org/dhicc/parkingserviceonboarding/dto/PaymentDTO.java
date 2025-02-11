package org.dhicc.parkingserviceonboarding.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDTO {
    private String vehicleNumber;
    private int amount;
    private LocalDateTime timestamp;
}