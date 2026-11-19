package com.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CounselingAppDocumentLoaderTest {

    @Resource
    private CounselingAppDocumentLoader counselingAppDocumentLoader;

    @Test
    void loadMarkdowns() {
        counselingAppDocumentLoader.loadMarkdowns();
    }
}