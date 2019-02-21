package com.trevisa.hexagon.onboarding.repository;

import com.trevisa.hexagon.onboarding.model.Registration;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RegistrationRepository extends ReactiveCrudRepository<Registration, ObjectId> {
}
