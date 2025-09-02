package chat.platform.plus.domain.chat.service.invoke.filter;

import chat.platform.plus.domain.chat.model.entity.HandleEntity;
import chat.platform.plus.domain.chat.model.entity.CheckEntity;
import chat.platform.plus.domain.chat.model.valobj.FilterEnum;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLLMFactory;
import chat.platform.plus.types.design.framework.link.AbstractLogicLink;
import chat.platform.plus.types.utils.FileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



/**
 * 文件过滤节点 - 文件体积
 */
@Slf4j
@Service
public class FileFilter extends AbstractLogicLink<CheckEntity, DefaultLLMFactory.DynamicContext, HandleEntity> {

    @Resource
    private FileUtil fileUtil;

    @Override
    public HandleEntity apply(CheckEntity checkEntity, DefaultLLMFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入文件过滤节点：{}", checkEntity.getUserId());
        // 上传文件体积
        if ((dynamicContext.getFile() != null && dynamicContext.getFile().getSize() > 200 * 1024 * 1024) ||
                (checkEntity.getFileListSize() != null && checkEntity.getFileListSize() > 200 * 1024 * 1024)) {
            log.info("文件过滤节点，文件体积太大，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_File_Size)
                    .build();
        }
        // 是否在此结束
        if (dynamicContext.getEndFilter() != null && dynamicContext.getEndFilter().equals(FilterEnum.FILE.getPlace())) {
            log.info("文件过滤节点，退出责任链：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .build();
        }
        return next(checkEntity, dynamicContext);
    }

}