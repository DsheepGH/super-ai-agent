package com.agent.superaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *获取当前日期和时间工具
 */
public class GetCurrentTimeTool {

    @Tool(description = "Get the current date and time")
    public String getCurrentTime() {

        //获取当前时间
        LocalDateTime now = LocalDateTime.now();

        //定义时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return now.format(dateTimeFormatter);
    }
}
