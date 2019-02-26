package com.trevisa.hexagon.onboarding.model;

import com.trevisa.hexagon.onboarding.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisWorkflow {

    private final RegistrationRepository registrationRepository;

    @EventListener
    public void onRegistrationCreated(RegistrationCreated event) {
        registrationRepository.findById(event.getRegistrationId())
                .map(registration -> authClient.execute(CreateAccount.builder()
                        .fullName(registration.getFullName())
                        .countryOfBirth(registration.getCountryOfBirth())
                        .documentNumber(registration.getDocument().getNumber())
                        .documentType(registration.getDocument().getType())
                        .dateOfBirth(registration.getDateOfBirth())
                        .build()))
                .map(result -> {
                    // approve registration
                    // send email
                })
                .doOnError(throwable -> {
                    if (throwable instanceof AccountAlreadyTakenException) {
                        // reject registration
                        // send email with rejection reason
                    }
                })
                .subscribe();
    }

}
