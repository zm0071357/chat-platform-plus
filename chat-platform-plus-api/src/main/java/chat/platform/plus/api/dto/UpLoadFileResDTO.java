package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传文件响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpLoadFileResDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 信息
     */
    private String message;

    /**
     * 文件详情
     */
    private File file;

    /**
     * 文件详情类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class File {

        /**
         * 文件URL
         */
        private String url;

        /**
         * 文件大小
         */
        private Long size;

        /**
         * 文件类型
         */
        private Integer fileType;
    }

}
