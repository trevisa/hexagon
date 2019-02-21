package com.trevisa.hexagon.onboarding.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

@Data
@RequiredArgsConstructor(onConstructor = @__(@PersistenceConstructor))
class Country {
    private final String alpha2;
}
