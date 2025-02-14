package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.dto.ParkingRecordDTO;
import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class ParkingControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    @Commit  // ✅ 테스트가 끝나도 데이터 유지
    void setUp() {
        parkingRecordRepository.deleteAll();
        subscriptionRepository.deleteAll();
    }

    /** ✅ 1. 차량 입차 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testRegisterEntry_Success() {
        // Given
        String vehicleNumber = "TEST123";

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/parking/entry/{vehicleNumber}",
                null,
                Map.class,
                vehicleNumber
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 🚗 입차 기록이 실제로 DB에 저장되었는지 확인
        ParkingRecord record = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNull(vehicleNumber)
                .orElse(null);
        assertNotNull(record);
        assertEquals(vehicleNumber, record.getVehicleNumber());
    }

    /** ✅ 2. 차량 출차 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testRegisterExit_Success() {
        // Given
        String vehicleNumber = "TEST456";
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber(vehicleNumber);
        record.setEntryTime(LocalDateTime.now().minusHours(3)); // 3시간 전 입차
        parkingRecordRepository.save(record);

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/parking/exit/{vehicleNumber}",
                null,
                Map.class,
                vehicleNumber
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 🚗 출차 기록이 정상적으로 저장되었는지 확인
        ParkingRecord updatedRecord = parkingRecordRepository.findByVehicleNumberAndExitTimeIsNotNull(vehicleNumber)
                .orElse(null);  // ✅ 데이터 없으면 null 반환

        assertNotNull(updatedRecord);  // 🚨 만약 출차 기록이 없다면, 테스트가 실패해야 함
        assertNotNull(updatedRecord.getExitTime()); // 출차 시간이 설정되어야 함
        assertTrue(updatedRecord.getFee() > 0); // 요금이 0보다 커야 함
    }

    /** ✅ 3. 주차 기록 조회 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetParkingRecords_Success() {
        // Given
        String vehicleNumber = "TEST789";
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber(vehicleNumber);
        record.setEntryTime(LocalDateTime.now().minusHours(2));
        record.setExitTime(LocalDateTime.now());
        record.setFee(5000);
        parkingRecordRepository.save(record);

        // When
        ResponseEntity<List<ParkingRecordDTO>> response = restTemplate.exchange(
                "/parking/{vehicleNumber}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ParkingRecordDTO>>() {},
                vehicleNumber
        );

        // 🚨 응답 검증 후 값 가져오기
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody()); // ✅ 응답 자체가 null인지 체크
        assertFalse(response.getBody().isEmpty()); // 🚀 데이터가 비어있지 않은지 확인
    }
}
