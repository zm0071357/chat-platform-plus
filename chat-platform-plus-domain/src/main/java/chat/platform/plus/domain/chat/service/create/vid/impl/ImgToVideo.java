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
 * 图生视频 - 基于首帧
 */
@Slf4j
@Service("img_to_video")
public class ImgToVideo extends AbstractCreateVidService {

    @Resource
    private CreatePort createPort;

    @Override
    protected CreateEntity create(CreateVidEntity createVidEntity) {
        // 构造参数
        VideoRequest videoRequest = VideoRequest.builder()
                .model("wanx2.1-i2v-turbo")
                .input(VideoRequest.InputExtend.builder()
                        .imgUrl(createVidEntity.getFileList().get(0).getUrl())
                        .prompt(createVidEntity.getContent())
                        .build())
                .parameters(VideoRequest.ParametersExtend.builder()
                        .promptExtend(true)
                        .resolution("720P")
                        .build())
                .build();
        return createPort.createVid(videoRequest);
    }

}
