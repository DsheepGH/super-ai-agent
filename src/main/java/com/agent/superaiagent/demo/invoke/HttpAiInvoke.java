package com.agent.superaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * HTTP方式调用AI
 */
public class HttpAiInvoke {

    public static void main(String[] args) {
        String url = "https://ws-x6akmpk8ouwk6l9w.cn-beijing.maas.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
        String apiKey = TestApiKey.Api_Key; // 从环境变量读取，与 curl 保持一致

        // 1. 构建 content 数组：先放图片，再放文本
        JSONArray contentArray = JSONUtil.createArray();
        contentArray.add(JSONUtil.createObj().put("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"));
        contentArray.add(JSONUtil.createObj().put("text", "图中描绘的是什么景象?"));

        // 2. 构建单条 message
        JSONObject message = JSONUtil.createObj()
                .put("role", "user")
                .put("content", contentArray);

        // 3. 构建 messages 数组
        JSONArray messages = JSONUtil.createArray().put(message);

        // 4. 构建最外层 input 对象
        JSONObject input = JSONUtil.createObj().put("messages", messages);

        // 5. 构建完整请求体
        JSONObject body = JSONUtil.createObj()
                .put("model", "qwen3.8-max")
                .put("input", input);

        // 6. 发起 POST 请求
        try (HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .execute()) {

            int status = response.getStatus();
            String result = response.body();
            System.out.println("HTTP 状态码: " + status);
            System.out.println("响应内容: " + result);
        }
    }
}