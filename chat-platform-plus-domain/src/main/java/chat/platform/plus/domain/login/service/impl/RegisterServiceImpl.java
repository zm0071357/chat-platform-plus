package chat.platform.plus.domain.login.service.impl;

import chat.platform.plus.domain.login.adapter.repository.RegisterRepository;
import chat.platform.plus.domain.login.model.entity.RegisterEntity;
import chat.platform.plus.domain.login.model.entity.RegisterResEntity;
import chat.platform.plus.domain.login.model.entity.VCEntity;
import chat.platform.plus.domain.login.service.RegisterService;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    @Resource
    private RegisterRepository registerRepository;

    @Override
    public RegisterResEntity register(RegisterEntity registerEntity) {
        RegisterResEntity registerResEntity = registerRepository.register(registerEntity);
        if (registerResEntity.getIsSuccess()) {
            // 登录
            StpUtil.login(registerResEntity.getUserId());
            log.info("登录成功:{}", registerResEntity.getUserId());
        }
        return registerResEntity;
    }

    @Override
    public VCEntity getVC(String userEmail) {
        return registerRepository.getVC(userEmail);
    }
}
