package com.agent.superaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuperAiAgentApplication {

    public static void main(String[] args) {
        // 环境变量没配时在日志最前面就提示，避免只看到一行 profile 就没了
        if (System.getenv("DASHSCOPE_API_KEY") == null || System.getenv("DASHSCOPE_API_KEY").isBlank()) {
            System.err.println("[启动检查] 未检测到环境变量 DASHSCOPE_API_KEY，"
                    + "spring.ai.dashscope.api-key 将无法解析，应用会启动失败。"
                    + "请在 云托管控制台 → 服务设置 → 环境变量 中配置。");
        }
        SpringApplication.run(SuperAiAgentApplication.class, args);
    }

}
