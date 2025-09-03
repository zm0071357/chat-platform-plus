package chat.platform.plus.domain.chat.adapter.port;

import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import qwen.sdk.largemodel.image.model.ImageRequest;
import qwen.sdk.largemodel.video.model.VideoRequest;

public interface CreatePort {

    /**
     * 图片创作
     * @param imageRequest
     * @return
     */
    CreateEntity createImg(ImageRequest imageRequest);

    /**
     * 图像编辑
     * @param imageRequest
     * @return
     */
    CreateEntity editImg(ImageRequest imageRequest);

    /**
     * 视频创作 - 文生视频和基于首帧
     * @param videoRequest
     * @return
     */
    CreateEntity createVid(VideoRequest videoRequest);

    /**
     * 视频创作 - 基于首尾帧
     * @param videoRequest
     * @return
     */
    CreateEntity createVidWithTwoFrame(VideoRequest videoRequest);
}
