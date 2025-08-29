package chat.platform.plus.domain.login.service.impl;

import chat.platform.plus.domain.login.adapter.repository.LoginRepository;
import chat.platform.plus.domain.login.adapter.repository.RegisterRepository;
import chat.platform.plus.domain.login.model.entity.LoginByPWEntity;
import chat.platform.plus.domain.login.model.entity.LoginByVCEntity;
import chat.platform.plus.domain.login.model.entity.LoginResEntity;
import chat.platform.plus.domain.login.model.entity.VCEntity;
import chat.platform.plus.domain.login.model.valobj.LoginConstant;
import chat.platform.plus.domain.login.service.LoginService;
import chat.platform.plus.types.utils.JavaMailUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private LoginRepository loginRepository;

    @Resource
    private RegisterRepository registerRepository;

    @Override
    public LoginResEntity loginByPW(LoginByPWEntity loginByPWEntity) {
        LoginResEntity loginResEntity = loginRepository.loginByPW(loginByPWEntity);
        log.info("通过账密登录:{}", loginResEntity.getUserId());
        if (loginResEntity.getIsSuccess()) {
            // 登录
            StpUtil.login(loginResEntity.getUserId());
            log.info("登录成功:{}", loginResEntity.getUserId());
        }
        return loginResEntity;
    }

    @Override
    public LoginResEntity loginByVC(LoginByVCEntity loginByVCEntity) {
        LoginResEntity loginResEntity = loginRepository.loginByVC(loginByVCEntity);
        log.info("通过邮箱登录:{}", loginByVCEntity.getUserEmail());
        if (loginResEntity.getIsSuccess()) {
            // 登录
            StpUtil.login(loginResEntity.getUserId());
            log.info("登录成功:{}", loginResEntity.getUserId());
        }
        return loginResEntity;
    }

    @Override
    public VCEntity getVC(String userEmail) {
        return registerRepository.getVC(userEmail);
    }

    @Override
    public LoginResEntity logout() {
        String userId = StpUtil.getLoginIdAsString();
        StpUtil.logout();
        log.info("退出登录:{}", userId);
        return LoginResEntity.builder()
                .userId(userId)
                .isSuccess(true)
                .message(LoginConstant.LogoutSuccess)
                .build();
    }
}
