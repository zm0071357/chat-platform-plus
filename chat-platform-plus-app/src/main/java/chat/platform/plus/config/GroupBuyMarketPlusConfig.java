package chat.platform.plus.config;

import chat.platform.plus.infrastructure.gateway.GroupBuyMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * 拼团营销服务配置
 */
@Slf4j
@Configuration
public class GroupBuyMarketPlusConfig {

    @Value("${app.config.groupbuy-market-plus.api_url}")
    private String groupBuyMarketPlusApiUrl;

    @Bean("groupBuyMarketService")
    public GroupBuyMarketService groupBuyMarketService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(groupBuyMarketPlusApiUrl)
                .addConverterFactory(JacksonConverterFactory.create()).build();
        log.info("拼团营销服务配置完成");
        return retrofit.create(GroupBuyMarketService.class);
    }

}
