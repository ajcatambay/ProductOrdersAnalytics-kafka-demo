package com.bridgingcode.ProductOrdersAnalytics;

import com.bridgingcode.ProductOrdersAnalytics.model.ProductOrdersEvent;
import com.bridgingcode.ProductOrdersAnalytics.processor.binding.ProductOrdersBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableBinding(ProductOrdersBindings.class)
public class ProductOrdersAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductOrdersAnalyticsApplication.class, args);
	}

	@Component
	public static class OrderSender implements ApplicationRunner {

		private static final Logger LOGGER = LoggerFactory.getLogger(OrderSender.class);

		private final MessageChannel productOrdersOut;

		public OrderSender(ProductOrdersBindings bindings) {
			this.productOrdersOut = bindings.productOrdersOut();
		}

		@Override
		public void run(ApplicationArguments args) throws Exception {

			HashMap<String, String[]> orders = new HashMap<>();
			orders.put("book", new String[]{"Hamlet", "Romeo and Juliet", "Sherlock Holmes"});
			orders.put("toy", new String[]{"Lego", "Hot Wheels", "Beyblade"});
			orders.put("food", new String[]{"Chicken burger", "Steak", "Pizza"});
			orders.put("video", new String[]{"Avengers: Age of Ultron", "Crazy Stupid Love", "Conjuring"});
			orders.put("music", new String[]{"Hybrid Theory", "Meteora", "Confessions"});

			Runnable runnable = () -> {
				String category = (String) orders.keySet().toArray()[new Random().nextInt(orders.size())];
				String name = orders.get(category)[new Random().nextInt(orders.get(category).length)];
				ProductOrdersEvent event = new ProductOrdersEvent();
				event.setName(name);
				event.setCategory(category);
				event.setQuantity(1);

				Message<ProductOrdersEvent> message = MessageBuilder
						.withPayload(event)
						.setHeader(KafkaHeaders.MESSAGE_KEY, UUID.randomUUID().toString().getBytes())
						.build();

				try {
					this.productOrdersOut.send(message);
				} catch (Exception e) {
					LOGGER.error("An error occurred", e);
				}
			};

			Executors.newScheduledThreadPool(1)
					.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
		}
	}

}
