package chat.platform.plus.domain.chat.adapter.port;

import chat.platform.plus.domain.chat.model.entity.RAGInvokeEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface RAGChatPort {

    /**
     * RAG对话
     * @param ragInvokeEntity
     * @param responseBodyEmitter
     * @return
     */
    ResponseBodyEmitter chat(RAGInvokeEntity ragInvokeEntity, ResponseBodyEmitter responseBodyEmitter);

    /**
     * 发送失败信息
     * @param result
     * @param responseBodyEmitter
     * @return
     */
    ResponseBodyEmitter fail(String result, ResponseBodyEmitter responseBodyEmitter);
}
