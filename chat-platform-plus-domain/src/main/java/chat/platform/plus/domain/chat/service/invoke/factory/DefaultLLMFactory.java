package chat.platform.plus.domain.chat.service.invoke.factory;

import chat.platform.plus.domain.chat.model.entity.LLMHandleEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.domain.chat.service.invoke.filter.DCCFilter;
import chat.platform.plus.domain.chat.service.invoke.filter.LLMFilter;
import chat.platform.plus.domain.chat.service.invoke.filter.UserFilter;
import chat.platform.plus.types.design.framework.link.LogicLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 大模型调用过滤工厂
 */
@Slf4j
@Service
public class DefaultLLMFactory {

    @Resource
    private DCCFilter dccFilter;

    @Resource
    private UserFilter userFilter;

    @Resource
    private LLMFilter llmFilter;

    /**
     * 组装责任链
     */
    public LogicLink<MessageEntity, DynamicContext, LLMHandleEntity> openLogicLink() {
        dccFilter.appendNext(userFilter);
        userFilter.appendNext(llmFilter);
        return dccFilter;
    }

    /**
     * 动态上下文
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {

        /**
         * 用户校验实体
         */
        private UserEntity userEntity;
    }

}
