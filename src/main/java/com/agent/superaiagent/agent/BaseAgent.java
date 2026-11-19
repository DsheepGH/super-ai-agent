package com.agent.superaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.agent.superaiagent.agent.model.AgentState;
import com.esotericsoftware.minlog.Log;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * 提供状态转换，内存管理和基于步骤的的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    //核心属性
    private String name;

    //提示词
    private String systemPrompt;
    private String nextStepPrompt;

    //代理状态
    private AgentState state = AgentState.IDLE;

    //执行步骤控制
    private int currentStep = 0;
    private int maxSteps = 40;

    //LLM大模型
    private ChatClient chatClient;

    //Memory记忆（需要自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    /**
     *运行代理
     *
     * @param userPrompt
     * @return
     */
    public String run(String userPrompt) {
        //1.基础校验
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        //2.执行，更改状态
        this.state = AgentState.RUNNING;
        //记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> results = new ArrayList<>();

        try {
            //执行循环
            for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxSteps);
                //单步执行
                String stepResult = step();
                String result = "Step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated : Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            Log.info("Error excuting agent", e);
            return "执行错误：" + e.getMessage();
        } finally {
            //3.清理资源
            this.cleanup();
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter实例
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建SseEmitter，设置较长的超时时间
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("错误：无法从状态运行代理: " + this.state);
                    emitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    emitter.send("错误：不能使用空提示词运行代理");
                    emitter.complete();
                    return;
                }

                // 更改状态
                state = AgentState.RUNNING;
                // 记录消息上下文
                messageList.add(new UserMessage(userPrompt));

                try {
                    boolean hasFinalResponse = false;
                    for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxSteps);

                        // 单步执行
                        String stepResult = step();
                        if (state == AgentState.FINISHED) {
                            // 只向用户发送最终回答，不暴露内部思考步骤和工具结果
                            emitter.send(stepResult);
                            hasFinalResponse = true;
                            break;
                        }
                        log.info("Step {}/{} completed: {}", stepNumber, maxSteps, stepResult);
                    }

                    if (!hasFinalResponse && state == AgentState.ERROR) {
                        emitter.send("这次处理没有成功完成，请稍后再试。");
                    } else if (!hasFinalResponse) {
                        state = AgentState.FINISHED;
                        emitter.send("我暂时无法完成这个请求，请换一种更具体的问法试试。");
                    }
                    // 正常完成
                    emitter.complete();
                } catch (Exception e) {
                    state = AgentState.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        emitter.send("执行错误: " + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                } finally {
                    // 清理资源
                    this.cleanup();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return emitter;
    }


    /**
     * 定义单个步骤
     * @return
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup() {
        //子类可以重写此方法来清理资源
    }
}
