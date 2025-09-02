package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 历史记录编码响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCodeResDTO {

    /**
     * 历史记录名称
     */
    private String historyName;

    /***
     * 历史记录编码
     */
    private String historyCode;

}
