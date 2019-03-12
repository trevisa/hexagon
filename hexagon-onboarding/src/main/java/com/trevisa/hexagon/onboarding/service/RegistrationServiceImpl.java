package com.trevisa.hexagon.onboarding.service;

import com.trevisa.hexagon.onboarding.model.Countries;
import com.trevisa.hexagon.onboarding.model.Registration;
import com.trevisa.hexagon.onboarding.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;

    @Override
    public Mono<Registration> create(RegistrationData registrationData) {
        return createEmpty()
                .map(registration -> updateRegistration(registration, registrationData))
                .flatMap(registrationRepository::save);
    }

    @Override
    public Mono<Registration> patch(String id, RegistrationData registrationData) {
        return registrationRepository.findById(new ObjectId(id))
                .map(registration -> updateRegistration(registration, registrationData))
                .flatMap(registrationRepository::save);
    }

    private Mono<Registration> createEmpty() {
        return registrationRepository.save(new Registration());
    }

    private Registration updateRegistration(Registration registration, RegistrationData registrationData) {
        if (!registration.canBeUpdated()) {
            throw new IllegalStateException("Registration can't be updated");
        }

        if (registrationData.getFullName() != null) {
            // TODO: validate full name
            registration.setFullName(registrationData.getFullName());
        }

        if (registrationData.getCountryOfResidence() != null) {
            registration.setCountryOfResidence(Countries.fromAlpha2(registrationData.getCountryOfResidence()));
        }

        if (registrationData.getDocumentNumber() != null || registrationData.getDocumentType() != null) {
            final Registration.Document document;

            if (registration.getDocument() == null) {
                document = new Registration.Document();
            } else {
                document = registration.getDocument();
            }

            if (registrationData.getDocumentType() != null) {
                document.setType(Registration.DocumentType.valueOf(registrationData.getDocumentType()));
            }

            if (document.getType() == null) {
                throw new IllegalStateException("Can't set documentNumber without specifying the type first");
            }

            if (!document.getType().getCountry().equals(registration.getCountryOfResidence())) {
                throw new IllegalArgumentException("Either the document type does not belong to the selected country of residence or the country is not set");
            }

            if (registrationData.getDocumentNumber() != null) {
                // TODO: validate document number
                document.setNumber(registrationData.getDocumentNumber());
            }

            registration.setDocument(document);
        }

        if (registrationData.getDateOfBirth() != null) {
            // TODO: validate birth date
            registration.setDateOfBirth(registrationData.getDateOfBirth());
        }

        registration.transitionState();

        return registration;
    }
}
