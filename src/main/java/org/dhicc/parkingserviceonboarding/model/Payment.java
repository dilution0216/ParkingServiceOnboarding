package org.dhicc.parkingserviceonboarding.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleNumber;

    // 최종 결제 금액
    @Column(nullable = false)
    private int amount;

    // 결제 시각
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // 적용된 할인 정보
    @Column(length = 255)
    private String discountDetails; // 적용된 할인 정보 (예: "야간 할인 20% + 쿠폰 할인 10%")


    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}