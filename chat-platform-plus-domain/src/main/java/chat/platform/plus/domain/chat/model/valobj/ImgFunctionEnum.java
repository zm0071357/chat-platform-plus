package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片指令枚举
 */
@Getter
@AllArgsConstructor
public enum ImgFunctionEnum {
    CREATE_IMAGE(1, "create_img", "生成图片"),
    DESCRIPTION_EDIT(2, "description_edit", "指令编辑"),
    REMOVE_WATERMARK(3, "remove_watermark", "去文字水印"),
    EXPAND(4, "expand", "扩图"),
    SUPER_RESOLUTION(5, "super_resolution","图像超分"),
    COLORIZATION(6, "super_resolution", "图像上色"),
    ;

    private Integer type;
    private String function;
    private String info;

    public static String getFunction(Integer type) {
        switch (type) {
            case 1:
                return CREATE_IMAGE.getFunction();
            case 2:
                return DESCRIPTION_EDIT.getFunction();
            case 3:
                return REMOVE_WATERMARK.getFunction();
            case 4:
                return EXPAND.getFunction();
            case 5 :
                return SUPER_RESOLUTION.getFunction();
            case 6:
                return COLORIZATION.getFunction();
            default:
                return null;
        }
    }
}
