package chat.platform.plus.domain.chat.service.invoke.factory;

import chat.platform.plus.domain.chat.model.entity.HandleEntity;
import chat.platform.plus.domain.chat.model.entity.CheckEntity;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.domain.chat.service.invoke.filter.DCCFilter;
import chat.platform.plus.domain.chat.service.invoke.filter.FileFilter;
import chat.platform.plus.domain.chat.service.invoke.filter.LLMFilter;
import chat.platform.plus.domain.chat.service.invoke.filter.UserFilter;
import chat.platform.plus.types.design.framework.link.LogicLink;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


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
    private FileFilter fileFilter;

    @Resource
    private LLMFilter llmFilter;

    /**
     * 组装责任链
     */
    public LogicLink<CheckEntity, DynamicContext, HandleEntity> openLogicLink() {
        dccFilter.appendNext(userFilter);
        userFilter.appendNext(fileFilter);
        fileFilter.appendNext(llmFilter);
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

        /**
         * 上传文件
         */
        private MultipartFile file;

        /**
         * 结束节点位置
         */
        private Integer endFilter;
    }

}
