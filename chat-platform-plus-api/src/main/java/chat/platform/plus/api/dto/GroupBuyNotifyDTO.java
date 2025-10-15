package chat.platform.plus.api.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * 成团结算回调请求体
 */
@Data
public class GroupBuyNotifyDTO {

    /**
     * 组队ID
     */
    private String teamId;

    /**
     * 外部交易单号集合
     */
    private List<String> outTradeNoList;
}
