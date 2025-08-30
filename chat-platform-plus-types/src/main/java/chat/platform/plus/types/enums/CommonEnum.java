package chat.platform.plus.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonEnum {

    LACK_PARM("10001", "缺少必填参数"),
    SUCCESS("10002", "调用成功"),
    UNKNOWN_ERROR("10003", "调用失败，未知错误"),
    ILLEGAL("10003", "参数非法")
    ;

    private String code;
    private String info;

}
