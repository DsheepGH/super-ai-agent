package com.agent.superaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void scrapWebPage() {

        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String url = "https://www.google.com";
        String webPage = webScrapingTool.scrapWebPage(url);
        Assertions.assertNotNull(webPage);
    }
}