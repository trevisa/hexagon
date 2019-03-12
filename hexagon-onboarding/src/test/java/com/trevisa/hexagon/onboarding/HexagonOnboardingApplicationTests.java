package com.trevisa.hexagon.onboarding;

import com.trevisa.hexagon.onboarding.model.Registration;
import com.trevisa.hexagon.onboarding.repository.RegistrationRepository;
import com.trevisa.hexagon.onboarding.service.RegistrationData;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@SpringBootTest
@EmbeddedKafka
public class HexagonOnboardingApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
 	private EmbeddedKafkaBroker kafkaEmbedded;

	@Test
	public void shouldCreateAnEmptyRegistration() {
		RegistrationData registrationData = new RegistrationData();

		FluxExchangeResult result = webTestClient.post()
				.uri("/v1/registrations")
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(registrationData)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().value("location", org.hamcrest.CoreMatchers.notNullValue())
				.returnResult(Void.class);

		String registrationId = getRegistrationId(result);

		Publisher<Registration> registrationPublisher = registrationRepository.findById(new ObjectId(registrationId));
		StepVerifier.create(registrationPublisher)
				.expectNextMatches((r) -> Registration.Status.PENDING_REQUIREMENTS.equals(r.getStatus()))
				.verifyComplete();
	}

	@Test
	public void shouldCompleteRegistration() {
		RegistrationData registrationData = new RegistrationData();

		FluxExchangeResult result = webTestClient.post()
				.uri("/v1/registrations")
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(registrationData)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().value("location", org.hamcrest.CoreMatchers.notNullValue())
				.returnResult(Void.class);

		String registrationId = getRegistrationId(result);

		registrationData = new RegistrationData();
		registrationData.setCountryOfResidence("BR");
		registrationData.setDateOfBirth(LocalDate.now());
		registrationData.setDocumentType("CPF");
		registrationData.setDocumentNumber("01234567890");
		registrationData.setFullName("Foo Bar");

		webTestClient.patch()
				.uri("/v1/registrations/{registrationId}", registrationId)
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(registrationData)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.countryOfResidence.alpha2").isEqualTo(registrationData.getCountryOfResidence())
				.jsonPath("$.document.type").isEqualTo(registrationData.getDocumentType())
				.jsonPath("$.document.number").isEqualTo(registrationData.getDocumentNumber())
				.jsonPath("$.fullName").isEqualTo(registrationData.getFullName())
				.jsonPath("$.status").isEqualTo(Registration.Status.AWAITING_ANALYSIS.name());

	}

	private String getRegistrationId(FluxExchangeResult result) {
		URI registrationLocation = result.getResponseHeaders().getLocation();

		Assertions.assertThat(registrationLocation).isNotNull();

		return registrationLocation.getPath().substring(registrationLocation.getPath().lastIndexOf('/') + 1);
	}
}
