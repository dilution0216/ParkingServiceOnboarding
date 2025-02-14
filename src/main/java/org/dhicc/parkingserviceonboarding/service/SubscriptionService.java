package org.dhicc.parkingserviceonboarding.service;

import lombok.RequiredArgsConstructor;
import org.dhicc.parkingserviceonboarding.dto.SubscriptionDTO;
import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.dhicc.parkingserviceonboarding.reposiotry.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public Subscription registerSubscription(String vehicleNumber, LocalDate startDate, LocalDate endDate) {
        subscriptionRepository.findByVehicleNumber(vehicleNumber)
                .ifPresent(sub -> {
                    throw new IllegalArgumentException("이미 등록된 차량입니다.");
                });

        Subscription subscription = new Subscription();
        subscription.setVehicleNumber(vehicleNumber);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        return subscriptionRepository.save(subscription);
    }

    public void cancelSubscription(String vehicleNumber) {
        Subscription subscription = subscriptionRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량의 정기권이 존재하지 않습니다."));

        subscriptionRepository.delete(subscription);
    }

    public boolean isSubscriptionValid(String vehicleNumber) {
        return subscriptionRepository.findByVehicleNumber(vehicleNumber)
                .map(sub -> !sub.getEndDate().isBefore(LocalDate.now()))
                .orElse(false);
    }

    public SubscriptionDTO getSubscription(String vehicleNumber) {
        return subscriptionRepository.findByVehicleNumber(vehicleNumber)
                .map(sub -> new SubscriptionDTO(
                        sub.getVehicleNumber(),
                        sub.getStartDate(),
                        sub.getEndDate()))
                .orElse(null);
    }

}