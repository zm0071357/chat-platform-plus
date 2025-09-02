package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点枚举
 */
@Getter
@AllArgsConstructor
public enum FilterEnum {

    DCC(1, "DCCFilter"),
    USER(2, "UserFilter"),
    FILE(3, "FileFilter"),
    LLM(4, "LLMFilter"),
    ;

    private Integer place;
    private String info;
}
