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
    TEXT_WITH_FILE(2, "文本带文件"),
    CREATE_IMAGE(3, "生成图片"),
    CREATE_VIDEO(4, "生成视频"),
    RAG(5, "RAG知识库"),
    DEFAULT(6, "默认"),
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
                return TEXT_WITH_FILE;
            case 3:
                return CREATE_IMAGE;
            case 4:
                return CREATE_VIDEO;
            case 5 :
                return RAG;
            default:
                return DEFAULT;
        }
    }

}
