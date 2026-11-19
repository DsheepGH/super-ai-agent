package com.agent.superaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

    public QueryRewriter(ChatModel dashScopeChatModel) {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashScopeChatModel);
        //创建查询重写转换器
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    /**
     * 执行查询重写
     * @param prompt
     * @return
     */
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        //执行查询重写
        Query transformedQuery = queryTransformer.transform(query);

        return transformedQuery.text();
    }
}
