package com.trevisa.hexagon.onboarding.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
public class Registration extends AbstractAggregateRoot<Registration> {

    @Id
    private ObjectId id;
    private Status status;
    private String fullName;
    private Document document;
    private LocalDate dateOfBirth;
    private Country countryOfResidence;

    public Registration() {
        this.status = Status.PENDING_REQUIREMENTS;
    }

    public boolean canBeUpdated() {
        return Status.PENDING_REQUIREMENTS.equals(this.status);
    }

    public void transitionState() {
        if (this.getStatus().equals(Status.PENDING_REQUIREMENTS)) {
            if (this.getFullName() != null &&
                this.getDateOfBirth() != null &&
                this.getCountryOfResidence() != null &&
                this.getDocument() != null &&
                this.getDocument().getType() != null &&
                this.getDocument().getNumber() != null) {
                this.setStatus(Status.AWAITING_ANALYSIS);
                this.registerEvent(new RegistrationWaitingAnalysisEvent(this.getId()));
            }
        }
    }

    public enum Status {
        PENDING_REQUIREMENTS,
        AWAITING_ANALYSIS,
        ;
    }

    @Data
    @NoArgsConstructor
    public static class Document {
        private DocumentType type;
        private String number;
    }

    @Getter
    public enum DocumentType {
        CPF(Countries.BR);

        private final Country country;

        DocumentType(Country country) {
            this.country = country;
        }
    }
}
