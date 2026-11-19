package com.agent.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
// PgVector 依赖外部 PostgreSQL，只在明确需要时通过 pgvector profile 启用
@Profile("pgvector")
public class PgVectorVectorStoreConfig {

    private static final int DASHSCOPE_EMBEDDING_BATCH_SIZE = 10;

    @Resource
    private CounselingAppDocumentLoader counselingAppDocumentLoader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        PgVectorStore pgVectorStore = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1024)
                .distanceType(COSINE_DISTANCE)
                .indexType(HNSW)
                .initializeSchema(true)
                .schemaName("public")
                .vectorTableName("vector_store")
                .maxDocumentBatchSize(10)
                .build();

        // DashScope Embedding API accepts at most 10 documents per request.
        List<Document> documents = counselingAppDocumentLoader.loadMarkdowns();
        for (int i = 0; i < documents.size(); i += DASHSCOPE_EMBEDDING_BATCH_SIZE) {
            int end = Math.min(i + DASHSCOPE_EMBEDDING_BATCH_SIZE, documents.size());
            pgVectorStore.add(documents.subList(i, end));
        }
        return pgVectorStore;
    }
}
