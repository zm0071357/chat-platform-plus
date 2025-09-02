package chat.platform.plus.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    IMAGE(1, "图片"),
    VIDEO(2, "视频"),
    AUDIO(3, "音频"),
    OTHER(4, "其他类型"),
    DEFAULT(5, "默认"),
    ;

    private Integer type;
    private String info;

    /**
     * 获取枚举值
     * @param type
     * @return
     */
    public static FileTypeEnum get(Integer type) {
        switch (type) {
            case 1:
                return IMAGE;
            case 2:
                return VIDEO;
            case 3:
                return AUDIO;
            case 4:
                return OTHER;
            default:
                return DEFAULT;
        }
    }

    /**
     * 返回多模态类型 - 图片、视频、音频
     */
    public static List<Integer> getMultiType() {
        return Arrays.asList(IMAGE.type, VIDEO.type, AUDIO.type);
    }
}
