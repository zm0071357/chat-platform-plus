package chat.platform.plus.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 订单类型枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OrderTypesEnum {

    COMMON(1, "直接购买"),
    GROUPBUY(2, "拼团购买"),
    ;

    private Integer type;
    private String desc;

    /**
     * 根据订单类型获取枚举
     * @param type 订单类型
     * @return
     */
    public static OrderTypesEnum get(Integer type) throws Exception {
        switch (type) {
            case 1:
                return COMMON;
            case 2:
                return GROUPBUY;
            default:
                throw new Exception("不存在的订单类型");
        }
    }

}
