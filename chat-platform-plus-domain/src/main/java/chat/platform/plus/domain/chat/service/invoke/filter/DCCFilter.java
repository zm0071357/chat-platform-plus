package chat.platform.plus.domain.chat.service.invoke.filter;

import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.LLMHandleEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLLMFactory;
import chat.platform.plus.types.design.framework.link.AbstractLogicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DCC过滤节点 - 服务降级、用户非空、切量
 */
@Slf4j
@Service
public class DCCFilter extends AbstractLogicLink<MessageEntity, DefaultLLMFactory.DynamicContext, LLMHandleEntity>  {

    @Resource
    private LLMRepository llmRepository;

    @Override
    public LLMHandleEntity apply(MessageEntity messageEntity, DefaultLLMFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入DCC过滤节点：{}", messageEntity.getUserId());
        // 服务降级拦截
        if (llmRepository.downgradeSwitch()) {
            log.info("DCC过滤节点，服务降级拦截，结束:{}", messageEntity.getUserId());
            //TODO: 在此抛出异常
            return LLMHandleEntity.builder()
                    .isSuccess(false)
                    .build();
        }
        // 用户非空校验
        UserEntity userEntity = llmRepository.getUserByUserId(messageEntity.getUserId());
        if (userEntity == null) {
            log.info("DCC过滤节点，用户为空，结束：{}", messageEntity.getUserId());
            //TODO: 在此抛出异常
            return LLMHandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_IllegalParam)
                    .build();
        }
        // 切量拦截
        if (!llmRepository.cutRange(userEntity.getUserId())) {
            log.info("DCC过滤节点，切量拦截，结束：{}", messageEntity.getUserId());
            //TODO: 在此抛出异常
            return LLMHandleEntity.builder()
                    .isSuccess(false)
                    .build();
        }
        // 设置动态上下文
        dynamicContext.setUserEntity(userEntity);
        // 流转到下一节点
        return next(messageEntity, dynamicContext);
    }

}
