package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public Subscription registerSubscription(String vehicleNumber, LocalDate startDate, LocalDate endDate) {
        // 🚨 정기권 중복 방지 (vehicleNumber는 UNIQUE)
        Optional<Subscription> existingSubscription = Optional.ofNullable(subscriptionRepository.findByVehicleNumber(vehicleNumber));
        if (existingSubscription.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 차량입니다.");
        }

        Subscription subscription = new Subscription();
        subscription.setVehicleNumber(vehicleNumber);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        return subscriptionRepository.save(subscription);
    }

    public void cancelSubscription(String vehicleNumber) {
        Subscription subscription = subscriptionRepository.findByVehicleNumber(vehicleNumber);
        if (subscription == null) {
            throw new IllegalArgumentException("해당 차량의 정기권이 존재하지 않습니다.");
        }
        subscriptionRepository.delete(subscription);
    }

    public boolean isSubscriptionValid(String vehicleNumber) {
        Subscription subscription = subscriptionRepository.findByVehicleNumber(vehicleNumber);
        return subscription != null && !subscription.getEndDate().isBefore(LocalDate.now());
    }
}
