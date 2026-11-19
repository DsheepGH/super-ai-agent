package com.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 心理咨询师向量数据库配置（初始化基于内存的向量数据库 Bean）
 */
@Configuration
public class CounselingAppVectorStoreConfig {

    @Resource
    private CounselingAppDocumentLoader counselingAppDocumentLoader;
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;
    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Lazy
    @Bean
    VectorStore counselingAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {

        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        // 加载文档
        List<Document> documents = counselingAppDocumentLoader.loadMarkdowns();
        // 自主切分
    //    List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
        //自动补充关键词元信息
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);

        simpleVectorStore.add(enrichedDocuments);

        return simpleVectorStore;
    }
}
