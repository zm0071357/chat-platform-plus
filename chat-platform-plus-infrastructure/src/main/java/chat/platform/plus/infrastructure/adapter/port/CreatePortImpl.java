package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.chat.adapter.port.CreatePort;
import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.sdk.largemodel.image.enums.ImageTaskStatusEnum;
import qwen.sdk.largemodel.image.impl.ImageServiceImpl;
import qwen.sdk.largemodel.image.model.ImageRequest;
import qwen.sdk.largemodel.image.model.ImageResponse;
import qwen.sdk.largemodel.image.model.ResultResponse;
import qwen.sdk.largemodel.video.impl.VideoServiceImpl;
import qwen.sdk.largemodel.video.model.VideoRequest;
import qwen.sdk.largemodel.video.model.VideoResponse;

import java.io.IOException;

@Slf4j
@Service
public class CreatePortImpl implements CreatePort {

    @Resource
    private ImageServiceImpl imageServiceImpl;

    @Resource
    private VideoServiceImpl videoServiceImpl;

    @Override
    public CreateEntity createImg(ImageRequest imageRequest) {
        try {
            ImageResponse imageResponse = imageServiceImpl.imageSynthesis(imageRequest);
            // 获取任务ID
            String taskId = imageResponse.getOutput().getTask_id();
            // 轮询获取结果
            ResultResponse result;
            String defaultStatus;
            // TODO:轮询必须优化
            do {
                result = imageServiceImpl.result(taskId);
                defaultStatus = result.getOutput().getTask_status();
            } while (!defaultStatus.equals(ImageTaskStatusEnum.SUCCEEDED.getCode()));
            String url = result.getOutput().getResults().get(0).getUrl();
            return CreateEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .type(1)
                    .url(url)
                    .build();
        } catch (IOException e) {
            return CreateEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Common)
                    .build();
        }
    }

    @Override
    public CreateEntity editImg(ImageRequest imageRequest) {
        try {
            ImageResponse imageResponse = imageServiceImpl.imageSynthesisByEdit(imageRequest);
            // 获取任务ID
            String taskId = imageResponse.getOutput().getTask_id();
            // 轮询获取结果
            ResultResponse result;
            String defaultStatus;
            // TODO:轮询必须优化
            do {
                result = imageServiceImpl.result(taskId);
                defaultStatus = result.getOutput().getTask_status();
            } while (!defaultStatus.equals(ImageTaskStatusEnum.SUCCEEDED.getCode()));
            String url = result.getOutput().getResults().get(0).getUrl();
            return CreateEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .type(1)
                    .url(url)
                    .build();
        } catch (IOException e) {
            return CreateEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Common)
                    .build();
        }
    }

    @Override
    public CreateEntity createVid(VideoRequest videoRequest) {
        try {
            VideoResponse videoResponse = videoServiceImpl.videoSynthesis(videoRequest);
            // 获取任务ID
            String taskId = videoResponse.getOutput().getTask_id();
            // 轮询获取结果
            qwen.sdk.largemodel.video.model.ResultResponse result;
            String defaultStatus;
            // TODO:轮询必须优化
            do {
                result = videoServiceImpl.result(taskId);
                defaultStatus = result.getOutput().getTask_status();
            } while (!defaultStatus.equals(ImageTaskStatusEnum.SUCCEEDED.getCode()));
            String url = result.getOutput().getVideo_url();
            return CreateEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .type(2)
                    .url(url)
                    .build();
        } catch (IOException e) {
            return CreateEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Common)
                    .build();
        }
    }

    @Override
    public CreateEntity createVidWithTwoFrame(VideoRequest videoRequest) {
        try {
            VideoResponse videoResponse = videoServiceImpl.videoSynthesisWithImages(videoRequest);
            // 获取任务ID
            String taskId = videoResponse.getOutput().getTask_id();
            // 轮询获取结果
            qwen.sdk.largemodel.video.model.ResultResponse result;
            String defaultStatus;
            // TODO:轮询必须优化
            do {
                result = videoServiceImpl.result(taskId);
                defaultStatus = result.getOutput().getTask_status();
            } while (!defaultStatus.equals(ImageTaskStatusEnum.SUCCEEDED.getCode()));
            String url = result.getOutput().getVideo_url();
            return CreateEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .type(2)
                    .url(url)
                    .build();
        } catch (IOException e) {
            return CreateEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_Common)
                    .build();
        }
    }
}
