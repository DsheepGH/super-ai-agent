package com.agent.superaiagent.demo.invoke;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;

/**
 * Spring AI 调用本地 Ollama 大模型。
 */
public class OllamaAiInvoke {

    public static void main(String[] args) {
        String baseUrl = System.getenv().getOrDefault("OLLAMA_BASE_URL", "http://localhost:11434");
        String modelName = System.getenv().getOrDefault("OLLAMA_MODEL", "qwen2.5:3b");

        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();
        OllamaChatOptions options = OllamaChatOptions.builder()
                .model(modelName)
                .build();

        ChatModel ollamaChatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(options)
                .build();

        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("你好，请介绍一下你自己。"))
                .getResult()
                .getOutput();

        System.out.println(assistantMessage.getText());
    }
}
