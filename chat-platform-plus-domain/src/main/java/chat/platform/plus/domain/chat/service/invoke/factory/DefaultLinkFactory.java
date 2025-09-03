package chat.platform.plus.domain.chat.service.invoke.factory;

import chat.platform.plus.domain.chat.model.entity.HandleEntity;
import chat.platform.plus.domain.chat.model.entity.CheckEntity;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.domain.chat.service.invoke.filter.*;
import chat.platform.plus.types.design.framework.link.multition.LinkArmory;
import chat.platform.plus.types.design.framework.link.multition.chain.BusinessLinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * 责任链建造工厂
 */
@Slf4j
@Service
public class DefaultLinkFactory {

    /**
     * 调用大模型责任链
     * @param dccFilter
     * @param userFilter
     * @param fileFilter
     * @param llmFilter
     * @return
     */
    @Bean("llmLogicLink")
    public BusinessLinkedList<CheckEntity, DynamicContext, HandleEntity> llmLogicLink(
            DCCFilter dccFilter, UserFilter userFilter, FileFilter fileFilter, LLMFilter llmFilter) {
        // 组装链
        LinkArmory<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> linkArmory =
                new LinkArmory<>("调用大模型过滤链", dccFilter, userFilter, fileFilter, llmFilter);
        // 链对象
        return linkArmory.getLogicLink();
    }

    /**
     * 图片创作责任链
     * @param dccFilter
     * @param userFilter
     * @param fileFilter
     * @param createImgFilter
     * @return
     */
    @Bean("createImgLogicLink")
    public BusinessLinkedList<CheckEntity, DynamicContext, HandleEntity> createImgLogicLink(
            DCCFilter dccFilter, UserFilter userFilter, FileFilter fileFilter, CreateImgFilter createImgFilter) {
        // 组装链
        LinkArmory<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> linkArmory =
                new LinkArmory<>("图片创作过滤链", dccFilter, userFilter, fileFilter, createImgFilter);
        // 链对象
        return linkArmory.getLogicLink();
    }

    /**
     * 视频创作责任链
     * @param dccFilter
     * @param userFilter
     * @param fileFilter
     * @param createVidFilter
     * @return
     */
    @Bean("createVidLogicLink")
    public BusinessLinkedList<CheckEntity, DynamicContext, HandleEntity> createVidLogicLink(
            DCCFilter dccFilter, UserFilter userFilter, FileFilter fileFilter, CreateVidFilter createVidFilter) {
        // 组装链
        LinkArmory<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> linkArmory =
                new LinkArmory<>("视频创作过滤链", dccFilter, userFilter, fileFilter, createVidFilter);
        // 链对象
        return linkArmory.getLogicLink();
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
         * 结束节点位置
         */
        private Integer endFilter;

        /**
         * 创作图片功能
         */
        private String imgFunction;

        /**
         * 创作视频功能
         */
        private String vidFunction;
    }

}
