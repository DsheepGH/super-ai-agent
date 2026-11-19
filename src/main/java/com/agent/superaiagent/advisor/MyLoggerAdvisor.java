package com.agent.superaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
import java.util.function.Function;


/**
 * 自定义日志 Advisor
 * 只打印info级别日志，只输出单次用户消息和AI回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    private final Function<ChatClientRequest, String> requestToString;
    private final Function<ChatResponse, String> responseToString;
    private final int order;

    public MyLoggerAdvisor(
            Function<ChatClientRequest, String> requestToString,
            Function<ChatResponse, String> responseToString,
            int order) {
        this.requestToString = requestToString;
        this.responseToString = responseToString;
        this.order = order;
    }


    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        this.logRequest(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        this.logResponse(chatClientResponse);
        return chatClientResponse;
    }

    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        this.logRequest(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    protected void logRequest(ChatClientRequest request) {
        log.info("USER request: {}", request.prompt().getUserMessage().getText());
    }

    protected void logResponse(ChatClientResponse chatClientResponse) {
        log.info("AI response: {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
    }

    public String getName() {

        return this.getClass().getSimpleName();
    }

    public int getOrder() {
        return order;
    }

    public static final class Builder {
        private Function<ChatClientRequest, String> requestToString;
        private Function<ChatResponse, String> responseToString;
        private int order = 0;

        private Builder() {
        }

        public MyLoggerAdvisor.Builder requestToString(Function<ChatClientRequest, String> requestToString) {
            this.requestToString = requestToString;
            return this;
        }

        public MyLoggerAdvisor.Builder responseToString(Function<ChatResponse, String> responseToString) {
            this.responseToString = responseToString;
            return this;
        }

        public MyLoggerAdvisor.Builder order(int order) {
            this.order = order;
            return this;
        }

        public MyLoggerAdvisor build() {
            return new MyLoggerAdvisor(this.requestToString, this.responseToString, this.order);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
