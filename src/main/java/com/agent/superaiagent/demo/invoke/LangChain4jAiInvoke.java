package com.agent.superaiagent.demo.invoke;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;

public class LangChain4jAiInvoke {
    public static void main(String[] args) {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.Api_Key)
                .modelName("qwen3.8-max")
                .build();

        String answer = qwenChatModel.generate("你好");
        System.out.println(answer);
    }
}
