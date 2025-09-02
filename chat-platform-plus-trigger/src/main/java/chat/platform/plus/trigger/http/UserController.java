package chat.platform.plus.trigger.http;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@SaCheckLogin
@RestController
@RequestMapping("/user")
public class UserController {



}
