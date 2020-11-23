package com.bridgingcode.ProductOrdersAnalytics.processor.binding;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface ProductOrdersBindings {
    String PRODUCT_ORDERS_IN = "poin";
    String PRODUCT_ORDERS_OUT = "poout";
    String PRODUCT_ORDERS_STREAMS_IN = "posin";
    String PRODUCT_ORDERS_ANALYTICS_IN = "pain";
    String PRODUCT_ORDERS_ANALYTICS_OUT = "paout";
    String PRODUCT_ORDERS_CATEGORY_COUNT = "PO-Category-count";

    @Input(PRODUCT_ORDERS_IN)
    SubscribableChannel productOrdersIn();

    @Output(PRODUCT_ORDERS_OUT)
    MessageChannel productOrdersOut();

    @Input(PRODUCT_ORDERS_STREAMS_IN)
    KStream<?, ?> productOrdersStreamsIn();

    @Input(PRODUCT_ORDERS_ANALYTICS_IN)
    KStream<?, ?> productOrdersAnalyticsIn();

    @Output(PRODUCT_ORDERS_ANALYTICS_OUT)
    KStream<?, ?> productOrdersAnalyticsOut();
}
