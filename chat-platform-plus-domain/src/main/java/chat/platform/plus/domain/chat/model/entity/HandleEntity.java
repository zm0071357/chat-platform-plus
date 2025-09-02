package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处理结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandleEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 处理结果
     */
    private String result;

    /**
     * 返回信息
     */
    private String message;

}
