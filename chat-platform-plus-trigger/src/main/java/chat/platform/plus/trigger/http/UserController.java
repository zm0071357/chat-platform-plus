package chat.platform.plus.trigger.http;

import chat.platform.plus.api.UserService;
import chat.platform.plus.api.dto.LoginResDTO;
import chat.platform.plus.api.dto.UserResDTO;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.login.model.entity.LoginResEntity;
import chat.platform.plus.domain.login.service.LoginService;
import chat.platform.plus.domain.login.service.RegisterService;
import chat.platform.plus.domain.user.model.entity.UserInfoEntity;
import chat.platform.plus.domain.user.service.UserActionService;
import chat.platform.plus.types.enums.CommonEnum;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController implements UserService {

    @Resource
    private LoginService loginService;

    @Resource
    private UserActionService userActionService;

    @SaCheckLogin
    @GetMapping("/info")
    @Override
    public Response<UserResDTO> userInfo() {
        UserInfoEntity userInfoEntity = userActionService.getUserInfo(StpUtil.getLoginIdAsString());
        if (userInfoEntity.getIsSuccess()) {
            return Response.<UserResDTO>builder()
                    .code(CommonEnum.SUCCESS.getCode())
                    .data(UserResDTO.builder()
                            .isSuccess(userInfoEntity.getIsSuccess())
                            .message(userInfoEntity.getMessage())
                            .userId(userInfoEntity.getUserId())
                            .userName(userInfoEntity.getUserName())
                            .userEmail(userInfoEntity.getUserEmail())
                            .build())
                    .info(CommonEnum.SUCCESS.getInfo())
                    .build();
        }
        return Response.<UserResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(UserResDTO.builder()
                        .isSuccess(userInfoEntity.getIsSuccess())
                        .message(userInfoEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @SaCheckLogin
    @PostMapping("/logout")
    @Override
    public Response<LoginResDTO> logout() {
        LoginResEntity logout = loginService.logout();
        return Response.<LoginResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(LoginResDTO.builder()
                        .isSuccess(logout.getIsSuccess())
                        .message(logout.getMessage())
                        .build())
                .build();
    }

    @PostMapping("/is_login")
    @Override
    public Response<Boolean> isLogin() {
        return Response.<Boolean>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(StpUtil.isLogin())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

}
