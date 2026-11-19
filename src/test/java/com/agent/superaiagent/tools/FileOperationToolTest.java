package com.agent.superaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String filePath = "心理常见问题.txt";
        String result = fileOperationTool.readFile(filePath);
        Assertions.assertNotNull(result);
    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String filePath = "心理常见问题.txt";
        String fileContent = "焦虑、抑郁、情绪失控（易怒、暴躁）、孤独感、过度担忧（灾难化思维）、精神内耗、职场/学业倦怠（压力大、拖延）、睡眠障碍（失眠）、强迫思维与强迫行为、成瘾行为（手机/网络成瘾）、社交恐惧、低自尊/自卑、讨好型人格、人际沟通困扰、原生家庭创伤。";
        String result = fileOperationTool.writeFile(filePath, fileContent);
        Assertions.assertNotNull(result);
    }
}
