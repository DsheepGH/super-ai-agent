package com.agent.superaiagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 创建自定义的 RAG检索增强顾问的工厂
 */
public class CounselingAppRagCustomAdvisorFactory {

    /**
     * 创建自定义的 RAG检索增强顾问
     * @param vectorStore
     * @param status
     * @return
     */
    public static Advisor create(VectorStore vectorStore, String status) {
        // 过滤特定状态的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .filterExpression(expression)       // 过滤条件
                        .similarityThreshold(0.50)      // 相似度阈值
                        .vectorStore(vectorStore)
                        .topK(3)        // 返回相似前3的文档
                        .build())
                .queryAugmenter(CounselingAppContextualQueryAugmenterFactory.create())
                .build();
    }
}
