package chat.platform.plus.domain.chat.service.create.vid.impl;

import chat.platform.plus.domain.chat.adapter.port.CreatePort;
import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.entity.CreateVidEntity;
import chat.platform.plus.domain.chat.service.create.vid.AbstractCreateVidService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.sdk.largemodel.video.enums.VideoModelEnum;
import qwen.sdk.largemodel.video.model.VideoRequest;

/**
 * 图生视频 - 基于首尾帧
 */
@Slf4j
@Service("imgs_to_video")
public class ImgsToVideo extends AbstractCreateVidService {

    @Resource
    private CreatePort createPort;

    @Override
    protected CreateEntity create(CreateVidEntity createVidEntity) {
        VideoRequest videoRequest = VideoRequest.builder()
                .model(VideoModelEnum.WANX_21_KF2V_PLUS.getModel())
                .input(VideoRequest.InputExtend.builder()
                        .firstFrameUrl(createVidEntity.getFileList().get(0).getUrl())
                        .lastFrameUrl(createVidEntity.getFileList().get(1).getUrl())
                        .prompt(createVidEntity.getContent())
                        .build())
                .parameters(VideoRequest.ParametersExtend.builder()
                        .promptExtend(true)
                        .resolution("720P")
                        .build())
                .build();
        return createPort.createVidWithTwoFrame(videoRequest);
    }

}
