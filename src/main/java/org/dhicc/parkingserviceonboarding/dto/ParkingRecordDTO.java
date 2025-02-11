package org.dhicc.parkingserviceonboarding.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ParkingRecordDTO {
    private String vehicleNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Integer fee;
}