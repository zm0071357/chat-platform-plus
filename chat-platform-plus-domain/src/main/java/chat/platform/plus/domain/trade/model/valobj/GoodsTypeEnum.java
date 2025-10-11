package chat.platform.plus.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 商品类型枚举
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum GoodsTypeEnum {

    COUNT(1, "count", "调用次数"),
    VIP(2, "vip", "VIP资格"),
    OTHER(3, "other", "其他")
    ;

    private Integer type;
    private String deliverStrategy;
    private String info;

    /**
     * 获取商品类型枚举
     * @param type 商品类型
     * @return
     */
    public static GoodsTypeEnum get(Integer type) {
        switch (type) {
            case 1:
                return COUNT;
            case 2:
                return VIP;
            case 3:
            default:
                return OTHER;
        }
    }
}
