package com.trevisa.hexagon.onboarding.service;

import com.trevisa.hexagon.onboarding.RegistrationData;
import com.trevisa.hexagon.onboarding.model.Countries;
import com.trevisa.hexagon.onboarding.model.Registration;
import com.trevisa.hexagon.onboarding.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public Mono<Registration> create(RegistrationData registrationData) {
        var registration = updateRegistration(new Registration(), registrationData);
        return registrationRepository.save(registration);
    }

    @Override
    public Mono<Registration> patch(String id, RegistrationData registrationData) {
        return registrationRepository.findById(new ObjectId(id))
                .map(registration -> updateRegistration(registration, registrationData))
                .flatMap(registrationRepository::save);
    }

    private Registration updateRegistration(Registration registration, RegistrationData registrationData) {
        if (!registration.canBeUpdated()) {
            throw new IllegalStateException("Registration can't be updated");
        }

        if (registrationData.getFullName() != null) {
            registration.setFullName(registrationData.getFullName());
        }

        if (registrationData.getDocumentNumber() != null || registrationData.getDocumentType() != null) {
            final Registration.Document document;

            if (registration.getDocument() == null) {
                document = new Registration.Document();
            } else {
                document = registration.getDocument();
            }

            if (registrationData.getDocumentNumber() != null) {
                document.setNumber(registrationData.getDocumentNumber());
            }

            if (registrationData.getDocumentType() != null) {
                document.setType(Registration.DocumentType.valueOf(registrationData.getDocumentType()));
            }

            registration.setDocument(document);
        }

        if (registrationData.getCountryOfBirth() != null) {
            registration.setCountryOfBirth(Countries.fromAlpha2(registrationData.getCountryOfBirth()));
        }

        if (registrationData.getDateOfBirth() != null) {
            registration.setDateOfBirth(registrationData.getDateOfBirth());
        }

        registration.transitionState((o, n) -> {
            kafkaTemplate.send("registration-status-changed", String.format("%s->%s", o, n));
        });

        return registration;
    }
}