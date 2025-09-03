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
 * 生成图片
 */
@Slf4j
@Service("create_img")
public class CreateImg extends AbstractCreateImgService {

    @Resource
    private CreatePort createPort;

    @Override
    protected CreateEntity create(CreateImgEntity createImgEntity) {
        // 构造参数
        ImageRequest imageRequest = ImageRequest.builder()
                .model(ImageEnum.WANX_21_T2I_TURBO.getModel())
                .input(ImageRequest.Input.builder()
                        .prompt(createImgEntity.getContent())
                        .build())
                .parameters(ImageRequest.Parameters.builder()
                        .prompt_extend(true)
                        .size(createImgEntity.getSize())
                        .n(1)
                        .build())
                .build();
        return createPort.createImg(imageRequest);
    }
}
