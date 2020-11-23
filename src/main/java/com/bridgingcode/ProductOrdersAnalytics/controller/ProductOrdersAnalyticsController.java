package com.bridgingcode.ProductOrdersAnalytics.controller;

import com.bridgingcode.ProductOrdersAnalytics.processor.binding.ProductOrdersBindings;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/shop")
public class ProductOrdersAnalyticsController {

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, String>> analytics() {
        Map<String, String> result = new LinkedHashMap<>();
        float total = 0l;

        ReadOnlyKeyValueStore<String, Long> keyValueStore =
                interactiveQueryService.getQueryableStore(
                        ProductOrdersBindings.PRODUCT_ORDERS_CATEGORY_COUNT,
                        QueryableStoreTypes.keyValueStore());

        KeyValueIterator<String, Long> iterator = keyValueStore.all();
        while (iterator.hasNext()) {
            KeyValue<String, Long> kv = iterator.next();
            total += kv.value;
        }
        result.put("totalOrders", total+"");

        KeyValueIterator<String, Long> iterator2 = keyValueStore.all();
        while (iterator2.hasNext()) {
            KeyValue<String, Long> kv = iterator2.next();
            result.put(kv.key, Math.round((kv.value.floatValue() / total) * 100) + "%");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
