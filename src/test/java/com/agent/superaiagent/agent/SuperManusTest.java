package com.agent.superaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SuperManusTest {

    @Resource
    private SuperManus superManus;

    @Test
    public void run() {
        String userPrompt = """
                我最近压力较大，想在上海静安区找一处安静、适合散步放松的公园，
                并结合网络信息，制定一份详细的周末减压计划，
                并以 PDF 格式输出
                """;
        String result = superManus.run(userPrompt);
        Assertions.assertNotNull(result);
    }
}
