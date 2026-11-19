package com.agent.superaiagent.controller;

import com.agent.superaiagent.agent.SuperManus;
import com.agent.superaiagent.app.CounselingApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private CounselingApp counselingApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI心理咨询师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/counseling/app_chat/sync")
    public String doChatWithCounselingAppSync(String message, String chatId) {

        return counselingApp.doChat(message, chatId);
    }

    /**
     * SSE流式调用 AI心理咨询师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/counseling_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithCounselingAppSSE(String message, String chatId) {

        return counselingApp.doChatByStream(message, chatId);
    }

    /**
     * SSE流式调用 AI心理咨询师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/counseling_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithCounselingAppServerSentEvent(String message, String chatId) {
        return counselingApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE流式调用 AI心理咨询师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/counseling_app/chat/sse_emitter")
    public SseEmitter doChatWithCounselingAppSseEmitter(String message, String chatId) {

        //创建一个超时时间较长的SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L);
        //获取Flux响应式数据流并且通过订阅推送给SseEmitter
         counselingApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (Exception e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);

         return sseEmitter;
    }

    /**
     * 流式调用 SuperManus超级智能体
     * @param message
     * @return
     */
    @GetMapping("super_manus/chat")
    public SseEmitter doChatWithSuperManus(String message) {
        SuperManus superManus = new SuperManus(allTools, dashscopeChatModel);
        return superManus.runStream(message);
    }
}
