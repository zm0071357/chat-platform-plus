package chat.platform.plus.api;

import chat.platform.plus.api.dto.ChatReqDTO;

public interface ChatService {

    String chat(ChatReqDTO chatReqDTO) throws Exception;
}
