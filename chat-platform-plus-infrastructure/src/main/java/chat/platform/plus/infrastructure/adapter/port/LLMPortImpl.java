package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.chat.adapter.port.LLMPort;
import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.InvokeEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageTypeEnum;
import chat.platform.plus.domain.chat.model.valobj.ResultConstant;
import chat.platform.plus.domain.chat.model.valobj.RoleEnum;
import chat.platform.plus.infrastructure.dao.HistoryDao;
import chat.platform.plus.infrastructure.dao.po.History;
import chat.platform.plus.types.common.File;
import chat.platform.plus.types.enums.FileTypeEnum;
import chat.platform.plus.types.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.enums.ChatModelEnum;
import qwen.sdk.largemodel.chat.impl.ChatServiceImpl;
import qwen.sdk.largemodel.chat.model.ChatMutiStreamResponse;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class LLMPortImpl implements LLMPort {

    @Resource
    private FileUtil fileUtil;

    @Resource
    private HistoryDao historyDao;

    @Resource
    private ChatServiceImpl chatServiceImpl;

    @Override
    public ResponseBodyEmitter chat(InvokeEntity invokeEntity, ResponseBodyEmitter responseBodyEmitter) {
        if (invokeEntity.getMessageType().equals(MessageTypeEnum.TEXT.getType()) ) {
            return commonChat(invokeEntity, responseBodyEmitter);
        } else if (invokeEntity.getMessageType().equals(MessageTypeEnum.TEXT_WITH_FILE.getType())) {
            return commonChatWithFile(invokeEntity, responseBodyEmitter);
        } else {
            return fail(ResultConstant.Fail_Illegal_Param, responseBodyEmitter);
        }
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

    /**
     * 普通文本
     * @param invokeEntity
     * @return
     */
    private ResponseBodyEmitter commonChat(InvokeEntity invokeEntity, ResponseBodyEmitter responseBodyEmitter) {
        // 构造用户消息
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text(invokeEntity.getContent())
                .build());
        // 获取多轮对话请求历史
        List<ChatRequest.Input.Message> requestMessages = invokeEntity.getRequestMessages();
        // 加入历史记录
        requestMessages.add(ChatRequest.Input.Message.builder()
                .role(RoleEnum.USER.getRole())
                .content(userContent)
                .build());
        // 构造参数
        ChatRequest request = ChatRequest.builder()
                .model(ChatModelEnum.QWEN_VL_MAX_LATEST.getModel())
                .input(ChatRequest.Input.builder()
                        .messages(requestMessages)
                        .build())
                .parameters(ChatRequest.Parameters.builder()
                        .resultFormat("message")
                        .enableSearch(invokeEntity.getIsSearch())
                        .incrementalOutput(true)
                        .searchOptions(invokeEntity.getIsSearch() ? ChatRequest.Parameters.SearchOptions.builder()
                                .enableSource(true)
                                .forcedSearch(true)
                                .build() : null)
                        .build())
                .build();
        log.info("请求参数:{}", JSON.toJSONString(request));
        return handle(responseBodyEmitter, invokeEntity.getUserId(), invokeEntity.getHistoryCode(),
                request, invokeEntity.getHistoryMessages(), requestMessages);
    }

    /**
     * 文本 + 文件
     * @param invokeEntity
     * @return
     */
    private ResponseBodyEmitter commonChatWithFile(InvokeEntity invokeEntity, ResponseBodyEmitter responseBodyEmitter) {
        // 构造用户消息
        List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
        userContent.add(ChatRequest.Input.Message.Content.builder()
                .text(invokeEntity.getContent())
                .build());
        // 组建用户消息
        for (File file : invokeEntity.getFileList()) {
            // 图片类型文件
            if (file.getType().equals(FileTypeEnum.IMAGE.getType())) {
                userContent.add(ChatRequest.Input.Message.Content.builder()
                        .image(file.getUrl())
                        .build());
            }
            // 图片类型文件
            else if (file.getType().equals(FileTypeEnum.AUDIO.getType())) {
                userContent.add(ChatRequest.Input.Message.Content.builder()
                        .audio(file.getUrl())
                        .build());
            }
            // 图片类型文件
            else if (file.getType().equals(FileTypeEnum.VIDEO.getType())) {
                userContent.add(ChatRequest.Input.Message.Content.builder()
                        .video(file.getUrl())
                        .build());
            }
            // 其他类型文件 - 解析出文本
            else if (file.getType().equals(FileTypeEnum.OTHER.getType())) {
                List<Document> documentList = fileUtil.getFileContent(file.getUrl());
                for (Document document : documentList) {
                    userContent.add(ChatRequest.Input.Message.Content.builder()
                            .text(document.getContent())
                            .build());
                }
            }
        }
        // 获取多轮对话请求历史
        List<ChatRequest.Input.Message> requestMessages = invokeEntity.getRequestMessages();
        // 加入历史记录
        requestMessages.add(ChatRequest.Input.Message.builder()
                .role(RoleEnum.USER.getRole())
                .content(userContent)
                .build());
        // 构造参数
        ChatRequest request = ChatRequest.builder()
                .model(ChatModelEnum.QWEN_VL_MAX_LATEST.getModel())
                .input(ChatRequest.Input.builder()
                        .messages(requestMessages)
                        .build())
                .parameters(ChatRequest.Parameters.builder()
                        .resultFormat("message")
                        .incrementalOutput(true)
                        .build())
                .build();
        log.info("请求参数:{}", JSON.toJSONString(request));
        return handle(responseBodyEmitter, invokeEntity.getUserId(), invokeEntity.getHistoryCode(),
                request, invokeEntity.getHistoryMessages(), requestMessages);
    }

    /**
     * 通用流式调用处理
     * @param userId 账号
     * @param historyCode 历史记录编码
     * @param request 请求参数
     * @param historyMessages 历史记录
     * @param requestMessages 历史请求记录
     * @return
     */
    private ResponseBodyEmitter handle(ResponseBodyEmitter responseBodyEmitter, String userId, String historyCode, ChatRequest request, List<ChatRequest.Input.Message> historyMessages, List<ChatRequest.Input.Message> requestMessages) {
        try {
            StringBuilder result = new StringBuilder();
            AtomicBoolean streamFailed = new AtomicBoolean(false); // 标记流是否失败
            AtomicBoolean resultFailed = new AtomicBoolean(false); // 标记回复是否失败
            chatServiceImpl.chatWithMultimodalWithStream(request, new EventSourceListener(){
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        ChatMutiStreamResponse response = JSON.parseObject(data, ChatMutiStreamResponse.class);
                        log.info("response:{}", JSON.toJSONString(response));
                        if (response.getOutput() != null && response.getOutput().getChoices().get(0).getFinish_reason().equals("null")) {
                            // 获取文本内容
                            String text = response.getOutput().getChoices().get(0).getMessage().getContent().get(0).getText();
                            log.info("回答片段:{}", text);
                            result.append(text);
                            responseBodyEmitter.send(text);
                        } else if (response.getOutput() != null && response.getOutput().getChoices().get(0).getFinish_reason().equals("stop")) {
                            log.info("回答终止:{}", data);
                        } else {
                            log.warn("回答失败:{}", data);
                            requestMessages.remove(requestMessages.size() - 1);
                            historyMessages.remove(historyMessages.size() - 1);
                            resultFailed.set(true); // 标记回答失败
                            responseBodyEmitter.send(ResultConstant.Fail);
                        }
                    } catch (Exception e) {
                        streamFailed.set(true); // 标记流失败
                        resultFailed.set(true); // 标记回答失败
                        responseBodyEmitter.completeWithError(new RuntimeException(ResultConstant.Fail, e));
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    if (!streamFailed.get() && !resultFailed.get()) {
                        log.info("result:{}", result);
                        // 将回答加入历史记录
                        List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
                        systemContent.add(ChatRequest.Input.Message.Content.builder()
                                .text(String.valueOf(result))
                                .build());
                        historyMessages.add(ChatRequest.Input.Message.builder()
                                .role(RoleEnum.ASSISTANT.getRole())
                                .content(systemContent)
                                .build());
                        requestMessages.add(ChatRequest.Input.Message.builder()
                                .role(RoleEnum.ASSISTANT.getRole())
                                .content(systemContent)
                                .build());
                        // 构造参数
                        History history = History.builder()
                                .userId(userId)
                                .historyCode(historyCode)
                                .historyJson(JSON.toJSONString(historyMessages))
                                .requestJson(JSON.toJSONString(requestMessages))
                                .build();
                        historyDao.update(history);
                    }
                    responseBodyEmitter.complete();
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    streamFailed.set(true);
                    log.error(t.getMessage());
                    responseBodyEmitter.completeWithError(new RuntimeException(ResultConstant.Fail, t));
                    requestMessages.remove(requestMessages.size() - 1);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return responseBodyEmitter;
    }

}
