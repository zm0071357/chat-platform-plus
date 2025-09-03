package chat.platform.plus.domain.chat.service.create.img.impl;

import chat.platform.plus.domain.chat.adapter.port.CreatePort;
import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.entity.CreateImgEntity;
import chat.platform.plus.domain.chat.service.create.img.AbstractCreateImgService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.sdk.largemodel.image.enums.ImageEnum;
import qwen.sdk.largemodel.image.model.ImageRequest;

/**
 * 扩图
 */
@Slf4j
@Service("expand")
public class Expand extends AbstractCreateImgService {

    @Resource
    private CreatePort createPort;

    @Override
    protected CreateEntity create(CreateImgEntity createImgEntity) {
        // 构造参数
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_IMAGEEDIT.getModel())
                .input(ImageRequest.InputExtend.builder()
                        .function(createImgEntity.getImgFunction())
                        .prompt(createImgEntity.getContent())
                        .build())
                .parameters(ImageRequest.ParametersExtend.builder()
                        .right_scale(1.5F)
                        .left_scale(1.5F)
                        .top_scale(1.5F)
                        .bottom_scale(1.5F)
                        .n(1)
                        .build())
                .build();
        return createPort.editImg(imageRequest);
    }
}
