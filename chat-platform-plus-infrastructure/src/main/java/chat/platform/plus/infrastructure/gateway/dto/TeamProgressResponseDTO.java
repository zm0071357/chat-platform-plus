package chat.platform.plus.infrastructure.gateway.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 拼团进度
 */
@Getter
@Setter
public class TeamProgressResponseDTO {

    /**
     * 拼团状态
     */
    private Integer status;

    /**
     * 目标量
     */
    private Integer targetCount;

    /**
     * 完成量
     */
    private Integer completeCount;

    /**
     * 锁单量
     */
    private Integer lockCount;

}
