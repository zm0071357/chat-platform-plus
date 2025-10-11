package chat.platform.plus.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OrderStatusEnum {
    CREATE(1, "创建完成"),
    PAY_WAIT(2, "等待支付"),
    PAY_SUCCESS(3, "支付成功"),
    DEAL_DONE(4, "交易完成"),
    CLOSE(5, "超时关单"),
    GROUP_BUY_COMPLETE(6, "拼团完成")
    ;

    private Integer status;
    private String desc;

    /**
     * 根据订单状态获取枚举
     * @param status 订单状态
     * @return
     */
    public static OrderStatusEnum get(Integer status) throws Exception {
        switch (status) {
            case 1:
                return CREATE;
            case 2:
                return PAY_WAIT;
            case 3:
                return PAY_SUCCESS;
            case 4:
                return DEAL_DONE;
            case 5:
                return CLOSE;
            case 6:
                return GROUP_BUY_COMPLETE;
            default:
                throw new Exception("不存在的订单状态");
        }
    }
}

