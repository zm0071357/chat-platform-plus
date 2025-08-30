package chat.platform.plus.trigger.http;

import chat.platform.plus.api.ChatService;
import chat.platform.plus.api.dto.ChatReqDTO;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.service.LLMService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/llm")
public class ChatController implements ChatService {

    @Resource
    private LLMService llmService;

    @PostMapping("/chat")
    @SaCheckLogin
    @Override
    public String chat(@RequestBody ChatReqDTO chatReqDTO) throws Exception {
        // 参数校验
        if (StringUtils.isBlank(chatReqDTO.getUserId()) || StringUtils.isBlank(chatReqDTO.getHistoryCode()) ||
                StringUtils.isBlank(chatReqDTO.getContent()) || chatReqDTO.getMessageType() == null) {
            return "fail";
        }
        MessageEntity messageEntity = MessageEntity.builder()
                .userId(chatReqDTO.getUserId())
                .historyCode(chatReqDTO.getHistoryCode())
                .content(chatReqDTO.getContent())
                .messageType(chatReqDTO.getMessageType())
                .build();
        return llmService.chat(messageEntity);
    }
}
