package com.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 AI的文档元信息增强器 （为文档补充元信息）
 */
@Component
public class MyKeywordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(dashscopeChatModel)
                .keywordCount(5)
                .build();

        return enricher.apply(documents);
    }
}
