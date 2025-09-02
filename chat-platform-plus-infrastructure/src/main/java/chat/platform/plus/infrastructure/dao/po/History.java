package chat.platform.plus.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 历史记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 账号
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 历史记录
     */
    private String historyJson;

    /**
     * 请求历史记录
     */
    private String requestJson;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
