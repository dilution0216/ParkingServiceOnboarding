package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.model.Subscription;
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

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class SubscriptionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    @Commit
    void setUp() {
        subscriptionRepository.deleteAll();
    }

    /** ✅ 1. 정기권 등록 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testRegisterSubscription_Success() {
        // Given
        Map<String, Object> request = Map.of(
                "vehicleNumber", "TEST123",
                "startDate", LocalDate.now().toString(),
                "endDate", LocalDate.now().plusMonths(1).toString()
        );

        // When
        ResponseEntity<Subscription> response = restTemplate.postForEntity(
                "/subscription/register",
                request,
                Subscription.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TEST123", response.getBody().getVehicleNumber());
    }

    /** ✅ 2. 중복 정기권 등록 방지 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testRegisterSubscription_Fail_AlreadyExists() {
        // Given
        Subscription existingSubscription = new Subscription();
        existingSubscription.setVehicleNumber("TEST123");
        existingSubscription.setStartDate(LocalDate.now());
        existingSubscription.setEndDate(LocalDate.now().plusMonths(1));
        subscriptionRepository.save(existingSubscription);

        Map<String, Object> request = Map.of(
                "vehicleNumber", "TEST123",
                "startDate", LocalDate.now().toString(),
                "endDate", LocalDate.now().plusMonths(1).toString()
        );

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/subscription/register",
                request,
                Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    /** ✅ 3. 정기권 취소 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testCancelSubscription_Success() {
        // Given
        Subscription subscription = new Subscription();
        subscription.setVehicleNumber("TEST456");
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscriptionRepository.save(subscription);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/subscription/cancel/{vehicleNumber}",
                HttpMethod.DELETE,
                null,
                String.class,
                "TEST456"
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(subscriptionRepository.findByVehicleNumber("TEST456").isPresent());
    }

    /** ✅ 4. 존재하지 않는 정기권 취소 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testCancelSubscription_Fail_NotFound() {
        // When
        ResponseEntity<Map> response = restTemplate.exchange(
                "/subscription/cancel/{vehicleNumber}",
                HttpMethod.DELETE,
                null,
                Map.class,
                "NON_EXISTENT"
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // ✅ 400 Bad Request 확인
        assertNotNull(response.getBody()); // ✅ 응답 본문이 null이 아님을 확인
        assertTrue(response.getBody().containsKey("error")); // ✅ "error" 키가 있는지 확인
        assertFalse(subscriptionRepository.findByVehicleNumber("NON_EXISTENT").isPresent()); // ✅ 존재하지 않아야 함
    }


    /** ✅ 5. 정기권 조회 테스트 */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testGetSubscription_Success() {
        // Given
        String vehicleNumber = "TEST789";
        Subscription subscription = new Subscription();
        subscription.setVehicleNumber(vehicleNumber);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscriptionRepository.save(subscription);

        // When
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/subscription/{vehicleNumber}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}, // ✅ Map<String, Object>로 받기
                vehicleNumber
        );

        // Then
        assertNotNull(response.getBody()); // ✅ 응답이 null인지 체크
        assertTrue(response.getBody().containsKey("data")); // ✅ "data" 키 존재 확인

        Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("data");
        assertNotNull(responseData); // ✅ 데이터가 null이 아닌지 체크
        assertEquals(vehicleNumber, responseData.get("vehicleNumber")); // ✅ 차량 번호가 올바른지 확인
    }

}
