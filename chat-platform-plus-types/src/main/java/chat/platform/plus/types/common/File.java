package chat.platform.plus.types.common;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File {

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件类型
     */
    private Integer type;

}
