package org.dhicc.parkingserviceonboarding.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SubscriptionDTO {
    private String vehicleNumber;
    private LocalDate startDate;
    private LocalDate endDate;
}