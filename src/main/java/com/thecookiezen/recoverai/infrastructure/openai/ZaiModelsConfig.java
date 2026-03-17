package com.thecookiezen.recoverai.infrastructure.openai;

import com.embabel.agent.openai.OpenAiCompatibleModelFactory;
import com.embabel.agent.spi.LlmService;
import com.embabel.common.ai.model.PricingModel;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

@Configuration
public class ZaiModelsConfig extends OpenAiCompatibleModelFactory {
    
    public ZaiModelsConfig(ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ClientHttpRequestFactory> requestFactory) {
        super(
            "https://api.z.ai/api/coding/paas/v4",
            System.getenv("ZAI_API_KEY"),
            "/chat/completions",
            null,
            java.util.Map.of(),
            observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
            requestFactory
        );
    }

    @Bean
    public LlmService<?> zaiModel() {
        return openAiCompatibleLlm(
            "glm-4-flash",
            PricingModel.usdPer1MTokens(1.0, 3.0),
            "Z.AI",
            null
        );
    }
}