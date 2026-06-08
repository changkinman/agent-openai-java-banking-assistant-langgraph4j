// Copyright (c) Microsoft. All rights reserved.
package com.microsoft.openai.samples.assistant.config;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Langchain4JConfiguration {

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;

    @Value("${deepseek.model.name:deepseek-chat}")
    private String deepseekModelName;

    @Value("${deepseek.base.url:https://api.deepseek.com}")
    private String deepseekBaseUrl;

    @Bean
    public ChatLanguageModel chatLanguageModel() {

        return OpenAiChatModel.builder()
                .apiKey(deepseekApiKey)
                .modelName(deepseekModelName)
                .baseUrl(deepseekBaseUrl)
                .temperature(0.3)
                .timeout(Duration.ofSeconds(120))
                .logRequests(true)
                .logResponses(true)
                .build();
    }


}
