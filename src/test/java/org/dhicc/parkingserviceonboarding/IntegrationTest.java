package org.dhicc.parkingserviceonboarding;

import org.dhicc.parkingserviceonboarding.model.ParkingRecord;
import org.dhicc.parkingserviceonboarding.model.Payment;
import org.dhicc.parkingserviceonboarding.reposiotry.ParkingRecordRepository;
import org.dhicc.parkingserviceonboarding.reposiotry.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IntegrationTest {

    @Autowired
    private ParkingRecordRepository parkingRecordRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void testFullFlow() {
        ParkingRecord record = new ParkingRecord();
        record.setVehicleNumber("TEST9876");
        record.setEntryTime(LocalDateTime.now().minusHours(3));
        record.setExitTime(LocalDateTime.now());
        record.setFee(5000);

        parkingRecordRepository.save(record);

        Payment payment = new Payment();
        payment.setVehicleNumber("TEST9876");
        payment.setAmount(record.getFee());
        payment.setTimestamp(LocalDateTime.now());

        paymentRepository.save(payment);

        Optional<Payment> savedPayment = paymentRepository.findById(payment.getId());

        assertTrue(savedPayment.isPresent());
        assertEquals(5000, savedPayment.get().getAmount());
    }
}
