package com.agent.superaiagent.app;

import com.agent.superaiagent.advisor.MyLoggerAdvisor;
import com.agent.superaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class CounselingApp {

    private static final String CHAT_MEMORY_CONVERSATION_ID_KEY = "counseling-chat-memory-conversation-id-key";
    private static final String CHAT_MEMORY_RETRIEVE_SIZE_KEY = "counseling-chat-memory-retrieve-size";
    private static final String COUNSELING_SYSTEM_PROMPT = """
            你是一名专业、温暖且富有共情力的心理咨询师。
            你的职责是倾听来访者的困扰，帮助其梳理情绪与想法，并提供科学、可行的心理调适建议。
            你不进行精神疾病诊断，也不替代医生或心理咨询师的面谈与治疗。
            当来访者表达自伤、伤人风险或处于紧急危机时，应优先建议其立即联系当地急救、危机干预热线或可信赖的亲友，并尽快寻求专业帮助。
            回答应清晰、克制、尊重来访者的自主性，不提供违法、有害或超出心理咨询范围的承诺。
            """;


    private final ChatClient chatClient;
    // 懒加载：避免启动时就调用 Embedding/大模型接口，缩短云托管健康检查前的启动时间
    @Lazy
    @Autowired
    @Qualifier("counselingAppVectorStore")
    private VectorStore counselingAppVectorStore;
    @Resource
    private Advisor counselingAppRagCloudAdvisor;
    // 生产环境默认不启用 pgvector，允许该 Bean 不存在
    @Autowired(required = false)
    @Qualifier("pgVectorVectorStore")
    private VectorStore pgVectorVectorStore;
    @Resource
    private QueryRewriter queryRewriter;
    // 生产环境默认关闭 stdio MCP，允许该 Bean 不存在
    @Autowired(required = false)
    private ToolCallbackProvider toolCallbackProvider;

    public CounselingApp(ChatModel dashscopeChatModel) {
        //初始化基于内存的对话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)   // 限制保留的历史消息条数
                .build();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(COUNSELING_SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(),
                                //自定义日志Advisor，可按需开启
                                MyLoggerAdvisor.builder()
                                        .order(0)
                                        .build()
                                //自定义推理增强Advisor，可按需开启
                                // ,new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * Ai基础对话 （支持多轮对话记忆）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();

        log.info("Chat response: {} ", content);

        return content;
    }

    /**
     * Ai基础对话 （支持多轮对话记忆，SSE流式输出）
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        Flux<String> content = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();

        log.info("Chat response: {} ", content);

        return content;
    }

    record CounselingReport(String title, List<String> suggestions) {}

    /**
     * Ai心理咨询报告功能 （结构化输出实战）
     * @param message
     * @param chatId
     * @return
     */
    public CounselingReport doChatWithReport(String message, String chatId) {
        CounselingReport counselingReport = chatClient.prompt()
                .system(COUNSELING_SYSTEM_PROMPT + """
                        每次对话后都要生成心理咨询结果，标题格式为“{用户名}的心理咨询报告”，
                        内容为具体、可执行且尊重来访者选择的建议列表。
                        """)
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(CounselingReport.class);

        log.info("Chat response: {} ", counselingReport);

        return counselingReport;
    }


    /**
     * 应用RAG知识库进行对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {

        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient.prompt()
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志
                .advisors(MyLoggerAdvisor.builder().order(0).build())
                // 应用本地RAG知识库问答
                .advisors(QuestionAnswerAdvisor.builder(counselingAppVectorStore).build())
                //应用RAG检索增强服务 （基于云知识库服务）
        //        .advisors(counselingAppRagCloudAdvisor)
                //应用RAG检索增强服务 （基于PgVector向量存储）
        //        .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                //应用自定义的RAG检索增强服务 （文档查询器 + 上下文增强器）
        //         .advisors(CounselingAppRagCustomAdvisorFactory.create(counselingAppVectorStore, "常见心理困扰"))
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("Chat response: {} ", content);

        return content;
    }

    //AI调用工具能力
    @Resource
    private ToolCallback[] allTools;

    /**
     * Ai心理咨询报告功能 （支持调用工具）
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //开启日志
                .advisors(MyLoggerAdvisor.builder().order(0).build())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("Chat response: {} ", content);

        return content;
    }


    /**
     * Ai心理咨询报告功能 （调用 MCP服务）
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message, String chatId) {
        if (toolCallbackProvider == null) {
            return "MCP 工具未启用：当前环境未配置可用的 MCP 服务（生产环境默认关闭 stdio MCP）。";
        }
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                //开启日志
                .advisors(MyLoggerAdvisor.builder().order(0).build())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();

        String content = chatResponse.getResult().getOutput().getText();
        log.info("Chat response: {} ", content);

        return content;
    }
}
