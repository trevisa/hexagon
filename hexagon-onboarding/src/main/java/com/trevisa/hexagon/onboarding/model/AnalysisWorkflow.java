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
    public void onRegistrationCreated(RegistrationWaitingAnalysisEvent event) {
        System.out.println(event);
    }

}
