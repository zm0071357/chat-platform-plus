package chat.platform.plus.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 团长退单补偿任务状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HeaderCompensateTaskEnum {

    INIT(0, "未完成"),
    COMPLETE(1, "已完成"),
    FAIL(2, "拼团取消，不进行补偿"),
    ;

    private Integer status;

    private String info;

    /**
     * 根据状态获取枚举
     * @param status
     * @return
     */
    public static HeaderCompensateTaskEnum get(Integer status) throws Exception {
        switch (status) {
            case 0:
                return INIT;
            case 1:
                return COMPLETE;
            case 2:
                return FAIL;
            default:
                throw new Exception("不存在的团长退单补偿任务状态");
        }
    }
}
