package com.trevisa.hexagon.onboarding.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.function.BiConsumer;

@Data
public class Registration {

    @Id
    private ObjectId id;
    private Status status;
    private String fullName;
    private Document document;
    private LocalDate dateOfBirth;
    private Country countryOfBirth;

    public Registration() {
        this.status = Status.PENDING_REQUIREMENTS;
    }

    public void transitionState(BiConsumer<Status, Status> consumer) {
        if (fullName != null
                && document != null
                && document.getType() != null
                && document.getNumber() != null
                && dateOfBirth != null
                && countryOfBirth != null
                && (status == null || Status.PENDING_REQUIREMENTS.equals(status))) {
            var currentStatus = this.getStatus();
            this.setStatus(Status.AWAITING_ANALYSIS);

            consumer.accept(currentStatus, this.getStatus());
        }
    }

    public boolean canBeUpdated() {
        return status == null || Status.PENDING_REQUIREMENTS.equals(status);
    }

    public enum Status {
        PENDING_REQUIREMENTS,
        AWAITING_ANALYSIS,
        ACTIVE,
        REJECTED,
        CANCELLED,
    }


    @Data
    @NoArgsConstructor
    public static class Document {
        private DocumentType type;
        private String number;

        public Document(String documentType, String documentNumber) {
            if (documentType != null) {
                this.type = DocumentType.valueOf(documentType);
            }

            this.number = documentNumber;
        }
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
