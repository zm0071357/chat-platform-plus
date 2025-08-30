package chat.platform.plus.domain.chat.service.invoke.filter;

import chat.platform.plus.domain.chat.model.entity.LLMHandleEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.ResultConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLLMFactory;
import chat.platform.plus.types.design.framework.link.AbstractLogicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户校验过滤节点 - 黑名单、可用次数
 */
@Slf4j
@Service
public class UserFilter extends AbstractLogicLink<MessageEntity, DefaultLLMFactory.DynamicContext, LLMHandleEntity> {

    @Override
    public LLMHandleEntity apply(MessageEntity messageEntity, DefaultLLMFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入用户校验过滤节点：{}", messageEntity.getUserId());
        UserEntity userEntity = dynamicContext.getUserEntity();
        // 黑名单过滤
        if (userEntity.getIsBlack() == 1) {
            log.info("用户校验过滤节点，黑名单用户，结束:{}", messageEntity.getUserId());
            return LLMHandleEntity.builder()
                    .isSuccess(false)
                    .result(ResultConstant.Fail_IsBlack)
                    .message(MessageConstant.Fail_IsBlack)
                    .build();
        }
        // 可用次数过滤 - VIP用户不受可用次数影响
        if (userEntity.getIsVIP() == 0 && userEntity.getCount() <= 0) {
            log.info("用户校验过滤节点，可用次数不足，结束:{}", messageEntity.getUserId());
            return LLMHandleEntity.builder()
                    .isSuccess(false)
                    .result(ResultConstant.Fail_Count)
                    .message(MessageConstant.Fail_Count)
                    .build();
        }
        return next(messageEntity, dynamicContext);
    }
}
