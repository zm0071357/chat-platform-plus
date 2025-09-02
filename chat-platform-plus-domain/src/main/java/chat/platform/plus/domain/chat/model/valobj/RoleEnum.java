package chat.platform.plus.domain.chat.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    ;

    private String role;

}
