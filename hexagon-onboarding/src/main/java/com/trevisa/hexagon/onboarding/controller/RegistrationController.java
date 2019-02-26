package com.trevisa.hexagon.onboarding.controller;

import com.trevisa.hexagon.onboarding.model.Registration;
import com.trevisa.hexagon.onboarding.repository.RegistrationRepository;
import com.trevisa.hexagon.onboarding.service.RegistrationData;
import com.trevisa.hexagon.onboarding.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegistrationRepository registrationRepository;

    @GetMapping("/{id}")
    public Mono<Registration> findOne(@PathVariable String id) {
        return registrationRepository.findById(new ObjectId(id));
    }

    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody @Valid RegistrationData data) {
        return registrationService.create(data)
                .map(registration -> UriComponentsBuilder
                        .fromPath("/v1/registrations")
                        .pathSegment(registration.getId().toString())
                        .build()
                        .toUri())
                .map(uri -> ResponseEntity.created(uri).build());
    }

    @PatchMapping("/{id}")
    public Mono<Registration> patch(@PathVariable String id, @RequestBody @Valid RegistrationData data) {
        return registrationService.patch(id, data);
    }
}
