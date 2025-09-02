package chat.platform.plus.domain.chat.adapter.port;

import chat.platform.plus.domain.chat.model.entity.InvokeEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface LLMPort {

    /**
     * 调用大模型
     * @param invokeEntity
     * @return
     */
    ResponseBodyEmitter chat(InvokeEntity invokeEntity, ResponseBodyEmitter responseBodyEmitter);

    /**
     * 输出处理失败结果
     * @param result 结果
     * @return
     */
    ResponseBodyEmitter fail(String result, ResponseBodyEmitter responseBodyEmitter);
}
