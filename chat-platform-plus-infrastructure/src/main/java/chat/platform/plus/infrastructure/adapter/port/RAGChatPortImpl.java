package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.chat.adapter.port.RAGChatPort;
import chat.platform.plus.domain.chat.model.entity.RAGInvokeEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.ResultConstant;
import chat.platform.plus.domain.chat.model.valobj.RoleEnum;
import chat.platform.plus.infrastructure.dao.HistoryDao;
import chat.platform.plus.infrastructure.dao.po.History;
import chat.platform.plus.types.common.Constants;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RAGChatPortImpl implements RAGChatPort {

    @Resource
    private HistoryDao historyDao;

    @Resource
    private OllamaChatClient ollamaChatClient;

    @Resource
    private PgVectorStore pgVectorStore;

    @Override
    public ResponseBodyEmitter chat(RAGInvokeEntity ragInvokeEntity, ResponseBodyEmitter responseBodyEmitter) {
        // 构造请求参数
        SearchRequest request = SearchRequest.query(ragInvokeEntity.getContent())
                .withTopK(5)
                .withFilterExpression("knowledge == '" + ragInvokeEntity.getRagTag() + "'");
        log.info("请求参数:{}", JSON.toJSONString(request));
        List<Document> documents = pgVectorStore.similaritySearch(request);
        for (Document document : documents) {
            log.info("document:{}", document.getContent());
        }
        String documentCollectors = documents.stream().map(Document::getContent).collect(Collectors.joining());
        Message ragMessage = new SystemPromptTemplate(Constants.SYSTEM_PROMPT).createMessage(Map.of("documents", documentCollectors));
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage(ragInvokeEntity.getContent()));
        messages.add(ragMessage);
        Flux<ChatResponse> flux = ollamaChatClient.stream(new Prompt(
                messages,
                OllamaOptions.create().withModel("deepseek-r1:1.5b")
        ));
        StringBuilder result = new StringBuilder();
        flux.subscribe(
                chatResponse -> {
                    try {
                        if (chatResponse.getResults() != null && chatResponse.getResults().get(0) != null) {
                            String text = chatResponse.getResults().get(0).getOutput().getContent();
                            log.info("回答片段:{}", text);
                            responseBodyEmitter.send(text);
                            result.append(text);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("发送失败", e);
                    }
                },
                error -> {
                    responseBodyEmitter.completeWithError(new RuntimeException(ResultConstant.Fail, error));
                },
                () -> {
                    responseBodyEmitter.complete();
                    // 写入历史记录
                    log.error("完整回复：{}", result);
                    List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
                    systemContent.add(ChatRequest.Input.Message.Content.builder()
                            .text(String.valueOf(result))
                            .build());
                    List<ChatRequest.Input.Message> historyMessage = ragInvokeEntity.getHistoryMessage();
                    historyMessage.add(ChatRequest.Input.Message.builder()
                            .role(RoleEnum.ASSISTANT.getRole())
                            .content(systemContent)
                            .build());
                    History history = History.builder()
                            .userId(ragInvokeEntity.getUserId())
                            .historyCode(ragInvokeEntity.getHistoryCode())
                            .historyJson(JSON.toJSONString(historyMessage))
                            .build();
                    historyDao.ragUpdate(history);
                }
        );
        return responseBodyEmitter;
    }

    @Override
    public ResponseBodyEmitter fail(String result, ResponseBodyEmitter responseBodyEmitter) {
        try {
            responseBodyEmitter.send(result);
            responseBodyEmitter.complete();
            return responseBodyEmitter;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
