package chat.platform.plus.domain.chat.service.create.vid.impl;

import chat.platform.plus.domain.chat.adapter.port.CreatePort;
import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.entity.CreateVidEntity;
import chat.platform.plus.domain.chat.model.valobj.VidSizeEnum;
import chat.platform.plus.domain.chat.service.create.vid.AbstractCreateVidService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.sdk.largemodel.video.enums.VideoModelEnum;
import qwen.sdk.largemodel.video.model.VideoRequest;

/**
 * 文生视频
 */
@Slf4j
@Service("text_to_video")
public class TextToVideo extends AbstractCreateVidService {

    @Resource
    private CreatePort createPort;

    @Override
    protected CreateEntity create(CreateVidEntity createVidEntity) {
        // 构造参数
        VideoRequest videoRequest = VideoRequest.builder()
                .model(VideoModelEnum.WANX_21_T2V_TURBO.getModel())
                .input(VideoRequest.Input.builder()
                        .prompt(createVidEntity.getContent())
                        .build())
                .parameters(VideoRequest.ParametersExtend.builder()
                        .promptExtend(true)
                        .size(VidSizeEnum.SIXTEEN_NINE_720P.getResolution())
                        .build())
                .build();
        return createPort.createVid(videoRequest);
    }

}
