package org.dhicc.parkingserviceonboarding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ParkingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //차량 번호
    @Column(nullable = false)
    private String vehicleNumber;

//    입차 시간이 없을수도 있음 (미인식)
//    @Column(nullable = false)
    private LocalDateTime entryTime;

    // 출차시간
    private LocalDateTime exitTime;
    // 요금
    private Integer fee;

    @ManyToOne
    @JoinColumn(name = "subscription_id",nullable = true )
    private Subscription subscription;
}
