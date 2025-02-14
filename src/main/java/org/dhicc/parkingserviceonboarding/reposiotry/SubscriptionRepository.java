package org.dhicc.parkingserviceonboarding.reposiotry;

import org.dhicc.parkingserviceonboarding.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByVehicleNumber(String vehicleNumber);
}