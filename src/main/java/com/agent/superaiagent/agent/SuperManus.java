package com.agent.superaiagent.agent;

import com.agent.superaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * 心理自助智能体（拥有自主规划与工具调用能力）
 */
@Component
public class SuperManus extends ToolCallAgent {

    public SuperManus(ToolCallback[] availableToos, ChatModel dashscopeChatModel) {
        super(availableToos);
        this.setName("SuperManus");
        String SYSTEM_PROMPT = """
                You are a warm, practical psychological self-care assistant.
                Help the user understand their emotions, stress, sleep, and everyday mental-health concerns
                with evidence-informed, actionable suggestions.
                Do not diagnose medical or psychiatric conditions, and do not replace professional care.
                If the user mentions self-harm or immediate danger, encourage them to contact local emergency
                services, a crisis hotline, or a trusted person right away.

                Answer ordinary questions directly and completely. Only use tools when they are genuinely
                needed for external information, file work, or another concrete action.
                Never delay a direct answer merely to demonstrate tool usage.
                Respond in the user's language.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Decide whether a tool is actually required for the user's current request.
                If the request can be answered directly, provide the complete final answer now without calling a tool.
                If tools are required, use only the necessary tools, then give a clear final answer based on their results.
                Do not mention internal tool names, step numbers, or agent mechanics to the user.
                When the response is complete, simply return the final answer; no termination tool is needed.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(10);
        //初始化AI对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(MyLoggerAdvisor.builder().order(0).build())
                .build();
        this.setChatClient(chatClient);
    }
}
