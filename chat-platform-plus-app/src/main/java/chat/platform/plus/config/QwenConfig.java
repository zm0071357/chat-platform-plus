package chat.platform.plus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qwen.sdk.factory.ModelFactory;
import qwen.sdk.factory.defaults.DefaultModelFactory;
import qwen.sdk.largemodel.chat.impl.ChatServiceImpl;
import qwen.sdk.largemodel.image.impl.ImageServiceImpl;
import qwen.sdk.largemodel.video.impl.VideoServiceImpl;

@Slf4j
@Configuration
@EnableConfigurationProperties(QwenConfigProperties.class)
public class QwenConfig {

    private final QwenConfigProperties properties;

    public QwenConfig(QwenConfigProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "modelFactory")
    public ModelFactory modelFactory(QwenConfigProperties properties) {
        qwen.sdk.factory.Configuration configuration = new qwen.sdk.factory.Configuration(properties.getApiKey());
        log.info("通义千问配置完成");
        return new DefaultModelFactory(configuration);
    }

    @Bean(name = "chatService")
    @ConditionalOnProperty(value = "qwen.sdk.config.enable", havingValue = "true", matchIfMissing = false)
    public ChatServiceImpl chatService(ModelFactory modelFactory) {
        log.info("对话服务装配完成");
        return modelFactory.chatService();
    }

    @Bean(name = "imageService")
    @ConditionalOnProperty(value = "qwen.sdk.config.enable", havingValue = "true", matchIfMissing = false)
    public ImageServiceImpl imageService(ModelFactory modelFactory) {
        log.info("图片创作服务装配完成");
        return modelFactory.imageService();
    }

    @Bean(name = "videoService")
    @ConditionalOnProperty(value = "qwen.sdk.config.enable", havingValue = "true", matchIfMissing = false)
    public VideoServiceImpl videoService(ModelFactory modelFactory) {
        log.info("视频创作服务装配完成");
        return modelFactory.videoService();
    }

}
