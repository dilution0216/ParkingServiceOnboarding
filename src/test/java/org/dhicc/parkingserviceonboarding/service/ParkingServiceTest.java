//package org.dhicc.parkingserviceonboarding.service;
//
//import org.dhicc.parkingserviceonboarding.config.PricingPolicy;
//import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
//import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
//import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@ExtendWith(MockitoExtension.class)
//class ParkingServiceTest {
//
//    @Mock
//    private ParkingRecordRepository parkingRecordRepository;
//
//    @Mock
//    private SubscriptionRepository subscriptionRepository;
//
//    @Mock
//    private PricingPolicy pricingPolicy;
//
//    @InjectMocks
//    private ParkingService parkingService;
//
//    @Test
//    void testRegisterEntry() {
//        ParkingRecord record = new ParkingRecord();
//        record.setVehicleNumber("TEST123");
//        record.setEntryTime(LocalDateTime.now());
//
//        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(record);
//        when(subscriptionRepository.findByVehicleNumber("TEST123")).thenReturn(null); // Mock 설정
//
//        ParkingRecord savedRecord = parkingService.registerEntry("TEST123");
//
//        assertNotNull(savedRecord);
//        assertEquals("TEST123", savedRecord.getVehicleNumber());
//    }
//
//    @Test
//    void testCalculateFee_BaseCase() {
//        LocalDateTime entryTime = LocalDateTime.of(2024, 2, 20, 10, 0);
//        LocalDateTime exitTime = LocalDateTime.of(2024, 2, 20, 10, 25);
//
//        when(pricingPolicy.getBaseFee()).thenReturn(1000); // Mock 설정
//
//        int fee = parkingService.calculateFee(entryTime, exitTime, Optional.empty());
//
//        assertEquals(1000, fee); // 기본 요금 적용 확인
//    }
//}
//
