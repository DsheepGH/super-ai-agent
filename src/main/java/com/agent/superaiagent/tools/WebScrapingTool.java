package com.agent.superaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 网页抓取工具
 */
public class WebScrapingTool {

    @Tool(description = "Scrape the content from a web page")
    public String scrapWebPage(@ToolParam(description = "Url of the web page to scrape") String url) {

        try {
            Document document = Jsoup.connect(url).get();
            return document.html();
        } catch (Exception e) {
            return "Error scraping web page： " + e.getMessage();
        }

    }
}
