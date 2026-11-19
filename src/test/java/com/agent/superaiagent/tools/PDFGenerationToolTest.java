package com.agent.superaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "心理咨询.pdf";
        String content = "心理咨询";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
