package chat.platform.plus.domain.chat.service.invoke.filter;


import chat.platform.plus.domain.chat.model.entity.HandleEntity;
import chat.platform.plus.domain.chat.model.entity.CheckEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.ResultConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.common.File;
import chat.platform.plus.types.design.framework.link.multition.handler.LogicHandler;
import chat.platform.plus.types.enums.FileTypeEnum;
import chat.platform.plus.types.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * 大模型过滤节点 - 联网搜索校验、多模态限制、文件类型校验
 */
@Slf4j
@Service
public class LLMFilter implements LogicHandler<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> {

    @Resource
    private FileUtil fileUtil;

    @Override
    public HandleEntity apply(CheckEntity checkEntity, DefaultLinkFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入大模型过滤节点：{}", checkEntity.getUserId());
        // 联网搜索
        if (checkEntity.getIsSearch() && checkEntity.getFileList() != null && !checkEntity.getFileList().isEmpty()) {
            log.info("大模型过滤节点，上传文件无法进行联网搜索，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Search_File)
                    .build();
        }
        // 文件
        if (checkEntity.getFileList() != null && !checkEntity.getFileList().isEmpty()) {
            Set<Integer> typeSet = new HashSet<>();
            for (File file : checkEntity.getFileList()) {
                // 文件类型校验
                if (!fileUtil.getFileTypeByStr(file.getUrl()).equals(file.getType())) {
                    log.info("大模型过滤节点，文件类型错误，结束：{}", checkEntity.getUserId());
                    return HandleEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.Fail_File_Type)
                            .build();
                }
                typeSet.add(file.getType());

            }
            // 多模态限制
            if (typeSet.size() > 2 && typeSet.containsAll(FileTypeEnum.getMultiType())) {
                log.info("大模型过滤节点，多模态限制，结束：{}", checkEntity.getUserId());
                return HandleEntity.builder()
                        .isSuccess(false)
                        .result(ResultConstant.Fail_MultiType)
                        .message(MessageConstant.Fail_MultiType)
                        .build();
            }
        }
        log.info("大模型过滤节点，责任链执行完成：{}", checkEntity.getUserId());
        return HandleEntity.builder()
                .isSuccess(true)
                .message(MessageConstant.Success)
                .build();
    }
}
