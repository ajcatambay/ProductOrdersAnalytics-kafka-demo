package com.bridgingcode.ProductOrdersAnalytics.processor;

import com.bridgingcode.ProductOrdersAnalytics.model.ProductOrdersEvent;
import com.bridgingcode.ProductOrdersAnalytics.processor.binding.ProductOrdersBindings;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class ProductOrdersProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductOrdersProcessor.class);

    @StreamListener(ProductOrdersBindings.PRODUCT_ORDERS_STREAMS_IN)
    @SendTo(ProductOrdersBindings.PRODUCT_ORDERS_ANALYTICS_OUT)
    public KStream<String, ProductOrdersEvent> process(KStream<String, ProductOrdersEvent> input) {
        return input.filter((key, value) -> value.getQuantity() > 0);
    }

    @StreamListener(ProductOrdersBindings.PRODUCT_ORDERS_ANALYTICS_IN)
    public void processAnalytics(KStream<String, ProductOrdersEvent> input) {
        input.map((key, value) -> new KeyValue<>(value.getCategory(), 0l))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .count(Materialized.as(ProductOrdersBindings.PRODUCT_ORDERS_CATEGORY_COUNT))
                .toStream()
                .print(Printed.toSysOut());
    }

    @StreamListener(ProductOrdersBindings.PRODUCT_ORDERS_IN)
    public void sink(ProductOrdersEvent productOrdersEvent) {
        LOGGER.info("New ProductOrderEvent. {} - {} - {}",
                productOrdersEvent.getName(),
                productOrdersEvent.getCategory(),
                productOrdersEvent.getQuantity());
    }
}
