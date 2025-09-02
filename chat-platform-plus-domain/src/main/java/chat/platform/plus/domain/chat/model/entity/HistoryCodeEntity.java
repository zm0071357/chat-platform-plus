package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 历史记录编码实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCodeEntity {

    /**
     * 历史记录名称
     */
    private String historyName;

    /***
     * 历史记录编码
     */
    private String historyCode;

}
