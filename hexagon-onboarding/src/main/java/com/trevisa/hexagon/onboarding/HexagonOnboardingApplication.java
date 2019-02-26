package com.trevisa.hexagon.onboarding;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.trevisa.hexagon.onboarding.model.RegistrationCreated;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class HexagonOnboardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HexagonOnboardingApplication.class, args);
	}

	public class ObjectIdSerializer extends StdSerializer<ObjectId> {

		ObjectIdSerializer() {
			super(ObjectId.class);
		}

		@Override
		public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.toHexString());
		}
	}

	@Bean
	Module objectIdModule() {
		var module = new SimpleModule("objectIdModule");

		module.addSerializer(new ObjectIdSerializer());

		return module;
	}


	@KafkaListener(topics = "registration-status-changed")
	void onRegistrationUpdated(ConsumerRecord<?, ?> cr) {
		log.info("God message: {}", cr);
	}
}
