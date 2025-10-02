package chat.platform.plus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ltzf.sdk.config", ignoreInvalidFields = true)
public class LTZFConfigProperties {

    /**
     * 应用者ID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 密钥
     */
    private String partnerKey;

    /**
     * 回调地址
     */
    private String notifyUrl;

}
