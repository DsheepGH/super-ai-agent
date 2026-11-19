package com.agent.superaiagent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 云托管健康检查接口。
 * 应用配置了 context-path: /api，所以实际访问地址是 /api/health。
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
