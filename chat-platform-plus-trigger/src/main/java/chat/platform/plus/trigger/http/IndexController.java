package chat.platform.plus.trigger.http;

import chat.platform.plus.api.IndexService;
import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.login.model.entity.*;
import chat.platform.plus.domain.login.service.LoginService;
import chat.platform.plus.domain.login.service.RegisterService;
import chat.platform.plus.types.enums.CommonEnum;
import chat.platform.plus.types.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/login")
public class IndexController implements IndexService {

    @Resource
    private LoginService loginService;

    @Resource
    private RegisterService registerService;

    @PostMapping("/by_pw")
    @Override
    public Response<LoginResDTO> loginByPW(@RequestBody LoginByPWReqDTO loginByPWReqDTO) {
        // 参数校验
        if (StringUtils.isBlank(loginByPWReqDTO.getPassword()) || StringUtils.isBlank(loginByPWReqDTO.getUserId())) {
            return Response.<LoginResDTO>builder()
                    .code(CommonEnum.LACK_PARM.getCode())
                    .info(CommonEnum.LACK_PARM.getInfo())
                    .build();
        }
        LoginByPWEntity loginByPWEntity = LoginByPWEntity.builder()
                .userId(loginByPWReqDTO.getUserId())
                .password(loginByPWReqDTO.getPassword())
                .build();
        LoginResEntity loginResEntity = loginService.loginByPW(loginByPWEntity);
        return Response.<LoginResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(LoginResDTO.builder()
                        .isSuccess(loginResEntity.getIsSuccess())
                        .message(loginResEntity.getMessage())
                        .userId(loginResEntity.getUserId())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/by_vc")
    @Override
    public Response<LoginResDTO> loginByVC(@RequestBody LoginByVCReqDTO loginByVCReqDTO) {
        // 参数非空校验
        if (StringUtils.isBlank(loginByVCReqDTO.getUserEmail()) || StringUtils.isBlank(loginByVCReqDTO.getVC())) {
            return Response.<LoginResDTO>builder()
                    .code(CommonEnum.LACK_PARM.getCode())
                    .info(CommonEnum.LACK_PARM.getInfo())
                    .build();
        }
        // 参数合法性校验
        if (!EmailUtil.check(loginByVCReqDTO.getUserEmail())) {
            return Response.<LoginResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        LoginByVCEntity loginByVCEntity = LoginByVCEntity.builder()
                .userEmail(loginByVCReqDTO.getUserEmail())
                .VC(loginByVCReqDTO.getVC())
                .build();
        LoginResEntity loginResEntity = loginService.loginByVC(loginByVCEntity);
        return Response.<LoginResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(LoginResDTO.builder()
                        .isSuccess(loginResEntity.getIsSuccess())
                        .message(loginResEntity.getMessage())
                        .userId(loginResEntity.getUserId())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/register")
    @Override
    public Response<RegisterResDTO> register(@RequestBody RegisterReqDTO registerReqDTO) {
        // 参数校验
        if (StringUtils.isBlank(registerReqDTO.getUserName()) || StringUtils.isBlank(registerReqDTO.getUserEmail()) ||
                StringUtils.isBlank(registerReqDTO.getPassword()) || StringUtils.isBlank(registerReqDTO.getVC())) {
            return Response.<RegisterResDTO>builder()
                    .code(CommonEnum.LACK_PARM.getCode())
                    .info(CommonEnum.LACK_PARM.getInfo())
                    .build();
        }
        // 参数合法性校验
        if (!EmailUtil.check(registerReqDTO.getUserEmail())) {
            return Response.<RegisterResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        RegisterEntity registerEntity = RegisterEntity.builder()
                .userName(registerReqDTO.getUserName())
                .password(registerReqDTO.getPassword())
                .userEmail(registerReqDTO.getUserEmail())
                .VC(registerReqDTO.getVC())
                .build();
        RegisterResEntity registerResEntity = registerService.register(registerEntity);
        return Response.<RegisterResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(RegisterResDTO.builder()
                        .isSuccess(registerResEntity.getIsSuccess())
                        .message(registerResEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/get_vc/{userEmail}")
    @Override
    public Response<VCResDTO> getVC(@PathVariable("userEmail") String userEmail) {
        // 参数校验
        if (!EmailUtil.check(userEmail)) {
            return Response.<VCResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        VCEntity vcEntity = registerService.getVC(userEmail);
        return Response.<VCResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(VCResDTO.builder()
                        .isSuccess(vcEntity.getIsSuccess())
                        .message(vcEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

}
