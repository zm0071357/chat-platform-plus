package chat.platform.plus.infrastructure.gateway.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 退单请求体
 */
@Getter
@Setter
public class RefundOrderRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 外部交易单号
     */
    private String outTradeNo;

    /**
     * 来源
     */
    private String source;

    /**
     * 渠道
     */
    private String channel;

}
