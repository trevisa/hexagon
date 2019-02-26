package com.trevisa.hexagon.onboarding.service;

import com.trevisa.hexagon.onboarding.model.Registration;
import reactor.core.publisher.Mono;

public interface RegistrationService {

    Mono<Registration> create(RegistrationData registrationData);

    Mono<Registration> patch(String id, RegistrationData registrationData);
}
