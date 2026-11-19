package com.agent.superaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Spring Ai框架调用 Ai 大模型 (阿里）
 */
@Component
@Profile("!prod") // 生产环境不执行 Demo，避免启动时调用大模型
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;


    @Override
    public void run(String... args) throws Exception {
        AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("hello!"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());
    }
}
