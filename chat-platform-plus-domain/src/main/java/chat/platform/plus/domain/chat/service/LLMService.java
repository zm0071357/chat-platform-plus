package chat.platform.plus.domain.chat.service;

import chat.platform.plus.domain.chat.model.entity.MessageEntity;

public interface LLMService {

    String chat(MessageEntity messageEntity) throws Exception;
}
