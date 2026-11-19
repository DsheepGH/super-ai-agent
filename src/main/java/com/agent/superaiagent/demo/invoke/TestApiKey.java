package com.agent.superaiagent.demo.invoke;

/**
 * 仅用于测试获取Api key
 */
public interface TestApiKey {

    // Api key
    String Api_Key = System.getenv("DASHSCOPE_API_KEY");
}
