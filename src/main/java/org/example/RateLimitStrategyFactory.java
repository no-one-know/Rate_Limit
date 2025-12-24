package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RateLimitStrategyFactory {

    public static List<RateLimitKeyStrategy> createStrategies(
            Map<String, StrategyProperties> configs
    ) {

        List<RateLimitKeyStrategy> strategies = new ArrayList<>();

        for (Map.Entry<String, StrategyProperties> entry : configs.entrySet()) {

            String name = entry.getKey();
            StrategyProperties props = entry.getValue();

            RateLimitConfig config =
                    new RateLimitConfig(props.getCapacity(), props.getRefillRate());

            switch (name) {
                case "ip" ->
                        strategies.add(new IpRateLimitStrategy(config, props.isEnabled()));

                case "api" ->
                        strategies.add(new ApiRateLimitStrategy(config, props.isEnabled()));

                case "api-key" ->
                        strategies.add(new ApiKeyRateLimitStrategy(config, props.isEnabled()));

                default ->
                        throw new IllegalArgumentException("Unknown strategy: " + name);
            }
        }

        return strategies;
    }
}
