package chat.platform.plus.domain.chat.service.invoke.filter;

import chat.platform.plus.domain.chat.model.entity.CheckEntity;
import chat.platform.plus.domain.chat.model.entity.HandleEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.VidFunctionEnum;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.common.File;
import chat.platform.plus.types.design.framework.link.multition.handler.LogicHandler;
import chat.platform.plus.types.enums.FileTypeEnum;
import chat.platform.plus.types.utils.FileUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 视频创作请求过滤节点 - 参考图数量校验、文件类型校验
 */
@Slf4j
@Service
public class CreateVidFilter implements LogicHandler<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> {

    @Resource
    private FileUtil fileUtil;

    @Override
    public HandleEntity apply(CheckEntity checkEntity, DefaultLinkFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入视频创作请求过滤节点：{}", checkEntity.getUserId());
        // 除了文生视频，其他功能都需要参考图
        if (!dynamicContext.getVidFunction().equals(VidFunctionEnum.TEXT_TO_VIDEO.getFunction()) &&
                (checkEntity.getFileList() == null || checkEntity.getFileList().isEmpty())) {
            log.info("视频创作请求过滤节点，缺少参考图，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Vid_Refer_Count)
                    .build();
        }
        // 图生视频 - 基于首帧，只允许一张参考图
        if (dynamicContext.getVidFunction().equals(VidFunctionEnum.IMG_TO_VIDEO.getFunction()) &&
                checkEntity.getFileList().size() != 1) {
            log.info("视频创作请求过滤节点，图生视频-基于首帧，参考图数量只允许为1，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Vid_Refers_Count + "1")
                    .build();
        }
        // 图生视频 - 基于首尾帧，只允许两张参考图
        if (dynamicContext.getVidFunction().equals(VidFunctionEnum.IMGS_TO_VIDEO.getFunction()) &&
                checkEntity.getFileList().size() != 2) {
            log.info("视频创作请求过滤节点，图生视频-基于首尾帧，参考图数量只允许为2，结束：{}", checkEntity.getUserId());
            return HandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Vid_Refers_Count + "2")
                    .build();
        }
        // 文件类型校验
        if (checkEntity.getFileList() != null && !checkEntity.getFileList().isEmpty()) {
            for (File file : checkEntity.getFileList()) {
                if (!fileUtil.getFileTypeByStr(file.getUrl()).equals(FileTypeEnum.IMAGE.getType())) {
                    log.info("视频创作请求过滤节点，文件类型错误，结束：{}", checkEntity.getUserId());
                    return HandleEntity.builder()
                            .isSuccess(false)
                            .message(MessageConstant.Fail_File_Type)
                            .build();
                }
            }
        }
        log.info("图片创作请求过滤节点，责任链执行完成：{}", checkEntity.getUserId());
        return HandleEntity.builder()
                .isSuccess(true)
                .message(MessageConstant.Success)
                .build();
    }
}
