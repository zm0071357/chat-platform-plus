package chat.platform.plus.domain.chat.service.invoke.filter;

import chat.platform.plus.domain.chat.model.entity.CheckEntity;
import chat.platform.plus.domain.chat.model.entity.HandleEntity;
import chat.platform.plus.domain.chat.model.valobj.ImgFunctionEnum;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.design.framework.link.multition.handler.LogicHandler;
import chat.platform.plus.types.enums.FileTypeEnum;
import chat.platform.plus.types.utils.FileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 图片创作请求过滤节点 - 参考图数量校验、文件类型校验
 */
@Slf4j
@Service
public class CreateImgFilter implements LogicHandler<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> {

    @Resource
    private FileUtil fileUtil;

    @Override
    public HandleEntity apply(CheckEntity checkEntity, DefaultLinkFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入图片创作请求过滤节点：{}", checkEntity.getUserId());
        // 除了生成图片，其它功能都需要提供参考图
        if (!dynamicContext.getImgFunction().equals(ImgFunctionEnum.CREATE_IMAGE.getFunction()) &&
                (checkEntity.getFileList() == null || checkEntity.getFileList().isEmpty())) {
            log.info("图片创作请求过滤节点，缺少参考图，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Img_Refer_Count)
                    .build();
        }
        // 参考图数量只能为1
        if (!dynamicContext.getImgFunction().equals(ImgFunctionEnum.CREATE_IMAGE.getFunction()) &&
                checkEntity.getFileList().size() != 1) {
            log.info("图片创作请求过滤节点，参考图数量不为1，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Img_Refers_Count)
                    .build();
        }
        // 文件类型校验
        if (!dynamicContext.getImgFunction().equals(ImgFunctionEnum.CREATE_IMAGE.getFunction()) && !fileUtil.getFileTypeByStr(checkEntity.getFileList().get(0).getUrl()).equals(FileTypeEnum.IMAGE.getType())) {
            log.info("图片创作请求过滤节点，文件类型错误，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_File_Type)
                    .build();
        }
        log.info("图片创作请求过滤节点，责任链执行完成：{}", checkEntity.getUserId());
        return HandleEntity.builder()
                .isSuccess(true)
                .message(MessageConstant.Success)
                .build();
    }

}
