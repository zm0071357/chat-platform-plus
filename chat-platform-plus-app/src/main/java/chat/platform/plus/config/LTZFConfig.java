package chat.platform.plus.config;

import lombok.extern.slf4j.Slf4j;
import ltzf.factory.PayFactory;
import ltzf.factory.defaults.DefaultPayFactory;
import ltzf.payments.nativepay.impl.NativePayServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 蓝兔支付配置
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(LTZFConfigProperties.class)
public class LTZFConfig {

    private final LTZFConfigProperties properties;

    public LTZFConfig(LTZFConfigProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "payFactory")
    public PayFactory payFactory(LTZFConfigProperties properties) {
        ltzf.factory.Configuration configuration = new ltzf.factory.Configuration(properties.getAppId(), properties.getMchId(), properties.getPartnerKey());
        log.info("蓝兔支付配置完成");
        return new DefaultPayFactory(configuration);
    }

    @Bean(name = "nativePayService")
    public NativePayServiceImpl nativePayService(PayFactory payFactory) {
        log.info("Native扫码支付服务装配完成");
        return payFactory.nativePayService();
    }


}
