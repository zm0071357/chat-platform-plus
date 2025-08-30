package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    TEXT(1, "普通文本"),
    CREATE_IMAGE(2, "生成图片"),
    CREATE_VIDEO(3, "生成视频"),
    RAG(4, "RAG知识库"),
    DEFAULT(5, "默认"),
    ;

    private Integer type;
    private String info;

    /**
     * 获取枚举值
     * @param type
     * @return
     */
    public static MessageTypeEnum get(Integer type) {
        switch (type) {
            case 1:
                return TEXT;
            case 2:
                return CREATE_IMAGE;
            case 3:
                return CREATE_VIDEO;
            case 4:
                return RAG;
            default:
                return DEFAULT;
        }
    }

}
