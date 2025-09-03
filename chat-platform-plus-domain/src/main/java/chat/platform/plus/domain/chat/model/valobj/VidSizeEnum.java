package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频尺寸枚举
 */
@Getter
@AllArgsConstructor
public enum VidSizeEnum {

    /**
     * wanx2.1-t2v-turbo 480P
     */
    SIXTEEN_NINE_480P("16:9", "832*480"),
    NINE_SIXTEEN_480P("9:16", "480*832"),
    ONE_ONE_480P("1:1", "624*624"),

    /**
     * wanx2.1-t2v-turbo 720P
     */
    SIXTEEN_NINE_720P("16:9", "1280*720"),
    NINE_SIXTEEN_720P("9:16", "720*1280"),
    ONE_ONE_720P("1:1", "960*960"),
    FOUR_THREE_720P("4:3", "1088*832"),
    THREE_FOUR_720P("3:4", "832*1088"),
    ;

    private String aspect_ratio;
    private String resolution;


}
