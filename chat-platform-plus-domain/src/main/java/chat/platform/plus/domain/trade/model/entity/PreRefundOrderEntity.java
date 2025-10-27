package chat.platform.plus.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * 预退单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreRefundOrderEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 退单原因
     */
    private String desc;

    /**
     * 生成退单订单ID
     * @return
     */
    public static String createRefundOrderId() {
        return "QDLL-TD".concat(RandomStringUtils.randomNumeric(12));
    }

}
