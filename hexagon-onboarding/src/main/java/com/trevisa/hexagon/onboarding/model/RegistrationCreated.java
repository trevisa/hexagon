package com.trevisa.hexagon.onboarding.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class RegistrationCreated {
    private final ObjectId registrationId;
}
