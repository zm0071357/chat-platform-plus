package chat.platform.plus.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 退单订单状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RefundOrderStatusEnum {

    INIT(1, "退款中"),
    SUCCESS(2, "退款成功"),
    FAIL(3, "退款失败"),
    ;

    private Integer status;
    private String info;

    /**
     * 根据状态获取枚举
     * @param status
     * @return
     */
    public static RefundOrderStatusEnum get(Integer status) throws Exception {
        switch (status) {
            case 1:
                return INIT;
            case 2:
                return SUCCESS;
            case 3:
                return FAIL;
            default:
                throw new Exception("不存在的退单订单状态");
        }
    }
}
