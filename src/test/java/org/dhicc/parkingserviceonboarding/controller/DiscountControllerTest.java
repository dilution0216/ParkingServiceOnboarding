package org.dhicc.parkingserviceonboarding.controller;

import org.dhicc.parkingserviceonboarding.model.DiscountCoupon;
import org.dhicc.parkingserviceonboarding.reposiotry.DiscountCouponRepository;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
public class DiscountControllerTest { // ❌ @Transactional 제거

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    @BeforeEach
    void setUp() {
        discountCouponRepository.deleteAll();

        DiscountCoupon coupon = new DiscountCoupon();
        coupon.setCouponCode("DISCOUNT10");
        coupon.setDiscountRate(10);
        discountCouponRepository.save(coupon);
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void testApplyDiscount_Success() {
        // Given
        String couponCode = "DISCOUNT10";
        int fee = 10000;
        int expectedDiscountedFee = 9000;

        System.out.println("✅ 테스트 시작 - couponCode: " + couponCode + ", fee: " + fee);

        // When
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/discount/apply/{couponCode}/{fee}",
                null,
                Map.class,
                couponCode, fee
        );

        System.out.println("✅ 응답 코드: " + response.getStatusCode());
        System.out.println("✅ 응답 본문: " + response.getBody());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDiscountedFee, response.getBody().get("discountedFee"));
    }
}




