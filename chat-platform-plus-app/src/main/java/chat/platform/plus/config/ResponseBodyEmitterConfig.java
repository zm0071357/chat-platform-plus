package chat.platform.plus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * ResponseBodyEmitter 客户端
 */
@Configuration
public class ResponseBodyEmitterConfig {

    @Bean("responseBodyEmitter")
    public ResponseBodyEmitter responseBodyEmitter() {
        return new ResponseBodyEmitter(10 * 60 * 1000L);
    }

}
