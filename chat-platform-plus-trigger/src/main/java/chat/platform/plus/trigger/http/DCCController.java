package chat.platform.plus.trigger.http;

import chat.platform.plus.api.DCCService;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.types.enums.CommonEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/dcc")
public class DCCController implements DCCService {

    @Resource
    private RTopic dccTopic;

    @PostMapping("/update_config")
    @Override
    public Response<Boolean> updateConfig(@RequestParam String key, @RequestParam String value) {
        try {
            dccTopic.publish(key + "," + value);
            return Response.<Boolean>builder()
                    .code(CommonEnum.SUCCESS.getCode())
                    .info(CommonEnum.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            return Response.<Boolean>builder()
                    .code(CommonEnum.UNKNOWN_ERROR.getCode())
                    .info(CommonEnum.UNKNOWN_ERROR.getInfo())
                    .build();
        }
    }
}
