package com.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyMultiQueryExpenderTest {

    @Resource
    private MyMultiQueryExpender myMultiQueryExpender;

    @Test
    void expand() {
        List<Query> queries = myMultiQueryExpender.expand("什么是企鹅？？？告诉我啦啦啦啦啦啦啦啦");
        Assertions.assertNotNull(queries);
    }
}