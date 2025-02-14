package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class PaymentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        parkingRecordRepository.deleteAll();
        subscriptionRepository.deleteAll();

        // 🚗 차량 출차 기록 추가 (결제 가능하도록 설정)
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber("123ABC");
        record.setEntryTime(LocalDateTime.now().minusHours(2)); // 2시간 전 입차
        record.setExitTime(LocalDateTime.now()); // 현재 출차
        record.setFee(5000); // 예상 요금 5000원
        parkingRecordRepository.save(record);
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testProcessPayment_Success() {
        // Given
        String vehicleNumber = "123ABC";

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/payment/process/{vehicleNumber}",
                null,
                Map.class,
                vehicleNumber
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("amount"));
        assertEquals(5000, response.getBody().get("amount"));
    }
}
