package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.HeaderCompensateTaskEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 退单补偿任务实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeaderCompensateTaskEntity {

    /**
     * 新团长用户ID
     */
    private String headerId;

    /**
     * 积分 - 用于后续营销动作
     */
    private Integer points;

    /**
     * 拼团组队ID
     */
    private String teamId;

    /**
     * 拼团组队状态
     */
    private Integer teamStatus;

    /**
     * 补偿状态枚举
     */
    private HeaderCompensateTaskEnum headerCompensateTaskEnum;

}
