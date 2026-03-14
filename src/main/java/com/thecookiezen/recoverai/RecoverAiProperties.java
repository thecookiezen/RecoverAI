package com.thecookiezen.recoverai;

import com.embabel.common.ai.model.LlmOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "recoverai")
public record RecoverAiProperties(@NestedConfigurationProperty LlmOptions chatLlm) {}