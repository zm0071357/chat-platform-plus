package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片尺寸枚举
 */
@Getter
@AllArgsConstructor
public enum ImgSizeEnum {
    ONE_ONE(1, "1024*1024", "1:1"),
    FOUR_THREE(2, "960*1440", "4:3"),
    NINE_SIXTEEN(3, "810*1440", "9:16"),
    SIXTEEN_NINE(4, "1440*810", "16:9"),
    ;

    private Integer type;
    private String size;
    private String info;

    /**
     * 获取尺寸 - size
     * @param sizeType
     * @return
     */
    public static String getSize(Integer sizeType) {
        switch (sizeType) {
            case 1:
                return ONE_ONE.getSize();
            case 2:
                return FOUR_THREE.getSize();
            case 3:
                return NINE_SIXTEEN.getSize();
            case 4:
                return SIXTEEN_NINE.getSize();
            default:
                return null;
        }
    }

    /**
     * 获取尺寸 - info
     * @param sizeType
     * @return
     */
    public static String getInfo(Integer sizeType) {
        switch (sizeType) {
            case 1:
                return ONE_ONE.getInfo();
            case 2:
                return FOUR_THREE.getInfo();
            case 3:
                return NINE_SIXTEEN.getInfo();
            case 4:
                return SIXTEEN_NINE.getInfo();
            default:
                return null;
        }
    }
}
