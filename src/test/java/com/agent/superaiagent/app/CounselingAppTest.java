package com.agent.superaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CounselingAppTest {
    @Resource
    private CounselingApp counselingApp;

    @Test
    void testChat() {
        String chaId = UUID.randomUUID().toString();

        //第一轮对话
        String answer = counselingApp.doChat("你好我是小明", chaId);
        Assertions.assertNotNull(answer);
        //第二轮对话
        answer = counselingApp.doChat("我最近睡不好，晚上总是胡思乱想。", chaId);
        Assertions.assertNotNull(answer);
        //第三轮对话
        answer = counselingApp.doChat("你还记得我刚才说睡不好吗？", chaId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void testDoChatWithReport() {
        String chaId = UUID.randomUUID().toString();
        String message = "你好我是小明，最近工作压力很大，经常失眠，我该怎么调整？";
        CounselingApp.CounselingReport counselingReport = counselingApp.doChatWithReport(message, chaId);

        Assertions.assertNotNull(counselingReport);
    }

    @Test
    void doChatWithRag() {
        String chaId = UUID.randomUUID().toString();
        String message = "我最近总是控制不住地焦虑，担心事情会变糟，我该怎么调整？";
        String answer = counselingApp.doChatWithRag(message, chaId);

        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        //testMessage("如何识别并缓解职场焦虑？");

        // 测试网页抓取：心理科普资料
        //testMessage("查看中国心理学会网站上的焦虑自助资料");

        // 测试资源下载：图片下载
        //testMessage("下载一张有助于放松的自然风景图片到文件");

        // 测试终端操作：执行代码
        //testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        //testMessage("保存我的情绪记录为文件");

        // 测试 PDF 生成
        //testMessage("生成一份‘压力管理练习计划’PDF，包含每日练习和记录表");

        // 测试 获取当前时间
        testMessage("现在北京是几点？");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = counselingApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我在上海静安区，请帮我查找附近的公园，用于散步放松";
        String answer =  counselingApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
