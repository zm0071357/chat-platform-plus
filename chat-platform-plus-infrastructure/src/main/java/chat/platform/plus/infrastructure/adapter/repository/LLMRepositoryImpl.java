package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.infrastructure.dao.UserDao;
import chat.platform.plus.infrastructure.dao.po.User;
import chat.platform.plus.infrastructure.dcc.DCCServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Slf4j
@Repository
public class LLMRepositoryImpl implements LLMRepository {

    @Resource
    private UserDao userDao;

    @Resource
    private DCCServiceImpl dccServiceImpl;

    @Override
    public UserEntity getUserByUserId(String userId) {
        User user = userDao.getUserByUserId(userId);
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .userId(user.getUserId())
                .isBlack(user.getIsBlack())
                .isVIP(user.getIsVip())
                .count(user.getCount())
                .build();
    }

    @Override
    public boolean downgradeSwitch() {
        return dccServiceImpl.isDowngradeSwitch();
    }

    @Override
    public boolean cutRange(String userId) {
        return dccServiceImpl.isCutRange(userId);
    }
}
