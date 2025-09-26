package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.chat.adapter.port.CreatePort;
import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.types.utils.AliOSSUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.sdk.largemodel.image.enums.ImageTaskStatusEnum;
import qwen.sdk.largemodel.image.impl.ImageServiceImpl;
import qwen.sdk.largemodel.image.model.ImageRequest;
import qwen.sdk.largemodel.image.model.ImageResponse;
import qwen.sdk.largemodel.video.enums.VideoTaskStatusEnum;
import qwen.sdk.largemodel.video.impl.VideoServiceImpl;
import qwen.sdk.largemodel.video.model.VideoRequest;
import qwen.sdk.largemodel.video.model.VideoResponse;

@Slf4j
@Service
public class CreatePortImpl implements CreatePort {

    @Resource
    private ImageServiceImpl imageServiceImpl;

    @Resource
    private VideoServiceImpl videoServiceImpl;

    // 最大尝试次数
    private static final int MAX_POLL_COUNT = 30;

    // 休眠时间
    private static final int POLL_SLEEP_TIME = 2000;

    @Override
    public CreateEntity createImg(ImageRequest imageRequest) {
        try {
            ImageResponse imageResponse = imageServiceImpl.imageSynthesis(imageRequest);
            String taskId = imageResponse.getOutput().getTask_id();
            return pollImageTask(taskId);
        } catch (Exception e) {
            return buildFailureResult(MessageConstant.Fail_Image);
        }
    }

    @Override
    public CreateEntity editImg(ImageRequest imageRequest) {
        try {
            ImageResponse imageResponse = imageServiceImpl.imageSynthesisByEdit(imageRequest);
            String taskId = imageResponse.getOutput().getTask_id();
            return pollImageTask(taskId);
        } catch (Exception e) {
            return buildFailureResult(MessageConstant.Fail_Image);
        }
    }

    @Override
    public CreateEntity createVid(VideoRequest videoRequest) {
        try {
            VideoResponse videoResponse = videoServiceImpl.videoSynthesis(videoRequest);
            String taskId = videoResponse.getOutput().getTask_id();
            return pollVideoTask(taskId);
        } catch (Exception e) {
            return buildFailureResult(MessageConstant.Fail_Video);
        }
    }

    @Override
    public CreateEntity createVidWithTwoFrame(VideoRequest videoRequest) {
        try {
            VideoResponse videoResponse = videoServiceImpl.videoSynthesisWithImages(videoRequest);
            String taskId = videoResponse.getOutput().getTask_id();
            return pollVideoTask(taskId);
        } catch (Exception e) {
            return buildFailureResult(MessageConstant.Fail_Video);
        }
    }

    /**
     * 轮询图片任务结果
     */
    private CreateEntity pollImageTask(String taskId) {
        for (int currentCount = 0; currentCount < MAX_POLL_COUNT; currentCount++) {
            try {
                qwen.sdk.largemodel.image.model.ResultResponse result = imageServiceImpl.result(taskId);
                String taskStatus = result.getOutput().getTask_status();

                if (taskStatus.equals(ImageTaskStatusEnum.FAILED.getCode()) ||
                        taskStatus.equals(ImageTaskStatusEnum.UNKNOWN.getCode())) {
                    return buildFailureResult(MessageConstant.Fail_Image);
                }

                if (taskStatus.equals(ImageTaskStatusEnum.SUCCEEDED.getCode())) {
                    String url = result.getOutput().getResults().get(0).getUrl();
                    String uploadStringUrl = AliOSSUtil.uploadStringUrl(url);
                    return buildSuccessResult(uploadStringUrl, 1);
                }

                // 等待后继续轮询
                if (currentCount < MAX_POLL_COUNT - 1) {
                    sleep();
                }
            } catch (Exception e) {
                return buildFailureResult(MessageConstant.Fail_Image);
            }
        }
        return buildFailureResult(MessageConstant.Fail_Image);
    }

    /**
     * 轮询视频任务结果
     */
    private CreateEntity pollVideoTask(String taskId) {
        for (int currentCount = 0; currentCount < MAX_POLL_COUNT; currentCount++) {
            try {
                qwen.sdk.largemodel.video.model.ResultResponse result = videoServiceImpl.result(taskId);
                String taskStatus = result.getOutput().getTask_status();
                if (taskStatus.equals(ImageTaskStatusEnum.FAILED.getCode()) ||
                        taskStatus.equals(ImageTaskStatusEnum.UNKNOWN.getCode())) {
                    return buildFailureResult(MessageConstant.Fail_Video);
                }
                if (taskStatus.equals(ImageTaskStatusEnum.SUCCEEDED.getCode())) {
                    String url = result.getOutput().getVideo_url();
                    String uploadStringUrl = AliOSSUtil.uploadStringUrl(url);
                    return buildSuccessResult(uploadStringUrl, 2);
                }
                // 等待后继续轮询
                if (currentCount < MAX_POLL_COUNT - 1) {
                    sleep();
                }
            } catch (Exception e) {
                return buildFailureResult(MessageConstant.Fail_Video);
            }
        }
        return buildFailureResult(MessageConstant.Fail_Video);
    }

    /**
     * 构建成功结果
     */
    private CreateEntity buildSuccessResult(String url, int type) {
        return CreateEntity.builder()
                .isSuccess(true)
                .message(MessageConstant.Success)
                .type(type)
                .url(url)
                .build();
    }

    /**
     * 构建失败结果
     */
    private CreateEntity buildFailureResult(String message) {
        return CreateEntity.builder()
                .isSuccess(false)
                .message(message)
                .build();
    }

    /**
     * 休眠方法
     */
    private void sleep() {
        try {
            Thread.sleep(POLL_SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e.getMessage());
        }
    }
}