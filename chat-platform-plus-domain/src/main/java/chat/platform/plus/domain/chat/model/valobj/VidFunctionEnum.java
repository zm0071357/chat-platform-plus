package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 创作视频类型枚举
 */
@Getter
@AllArgsConstructor
public enum VidFunctionEnum {

    TEXT_TO_VIDEO(1, "text_to_video", "文生视频"),
    IMG_TO_VIDEO(2, "img_to_video", "图生视频 - 基于首帧"),
    IMGS_TO_VIDEO(3, "imgs_to_video", "图生视频 - 基于首尾帧"),
    ;

    private Integer type;
    private String function;
    private String info;

    /**
     * 获取类型
     * @param vidFunction
     * @return
     */
    public static String getFunction(Integer vidFunction) {
        switch (vidFunction) {
            case 1:
                return TEXT_TO_VIDEO.getFunction();
            case 2:
                return IMG_TO_VIDEO.getFunction();
            case 3:
                return IMGS_TO_VIDEO.getFunction();
            default:
                return null;
        }
    }
}
