package chat.platform.plus.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 团长退单补偿任务
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeaderCompensateTask {

    /**
     * 自增ID
     */
    private Long id;

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
     * 补偿状态 - 0未完成、1已完成、2拼团取消，不进行补偿
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
