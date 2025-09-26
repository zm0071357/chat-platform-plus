package chat.platform.plus.domain.user.service;

import chat.platform.plus.domain.user.adapter.repository.UserActionRepository;
import chat.platform.plus.domain.user.model.entity.UserInfoEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserActionServiceImpl implements UserActionService{

    @Resource
    private UserActionRepository userActionRepository;

    @Override
    public UserInfoEntity getUserInfo(String userId) {
        return userActionRepository.getUserInfo(userId);
    }
}
