package chat.platform.plus.api.dto;

import lombok.Getter;

/**
 * 解析Git仓库请求体
 */
@Getter
public class GitReqDTO {

    /**
     * Git仓库地址
     */
    private String repoUrl;

    /**
     * 知识库名称
     */
    private String ragName;
}
