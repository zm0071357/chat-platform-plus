package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.login.adapter.repository.LoginRepository;
import chat.platform.plus.domain.login.model.entity.LoginByPWEntity;
import chat.platform.plus.domain.login.model.entity.LoginByVCEntity;
import chat.platform.plus.domain.login.model.entity.LoginResEntity;
import chat.platform.plus.domain.login.model.valobj.LoginConstant;
import chat.platform.plus.domain.login.model.valobj.RegisterConstant;
import chat.platform.plus.infrastructure.dao.UserDao;
import chat.platform.plus.infrastructure.dao.po.User;
import chat.platform.plus.types.common.MailConstant;
import chat.platform.plus.types.utils.AgronUtil;
import chat.platform.plus.types.utils.JavaMailUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;
import jakarta.annotation.Resource;

@Slf4j
@Repository
public class LoginRepositoryImpl implements LoginRepository {

    @Resource
    private UserDao userDao;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private JavaMailUtil javaMailUtil;

    @Resource
    private AgronUtil agronUtil;

    @Override
    public LoginResEntity loginByPW(LoginByPWEntity loginByPWEntity) {
        User existUser = userDao.getUserByUserId(loginByPWEntity.getUserId());
        // 用户不存在
        if (existUser == null) {
            return LoginResEntity.builder()
                    .isSuccess(false)
                    .message(LoginConstant.Fail)
                    .build();
        }
        // 密码不正确
        if (!agronUtil.verifyPassword(existUser.getPassword(), loginByPWEntity.getPassword())) {
            return LoginResEntity.builder()
                    .isSuccess(false)
                    .message(LoginConstant.Fail)
                    .build();
        }
        // 黑名单
        if(existUser.getIsBlack() == 1) {
            return LoginResEntity.builder()
                    .isSuccess(false)
                    .message(LoginConstant.Black)
                    .build();
        }
        return LoginResEntity.builder()
                .isSuccess(true)
                .userId(existUser.getUserId())
                .message(LoginConstant.Success)
                .build();
    }

    @Override
    public LoginResEntity loginByVC(LoginByVCEntity loginByVCEntity) {
        String key = LoginByVCEntity.getVCKey(loginByVCEntity.getUserEmail());
        String isExistVC = (String) redissonClient.getBucket(key).get();
        // 验证码是否过期
        if (isExistVC == null) {
            return LoginResEntity.builder()
                    .isSuccess(false)
                    .message(LoginConstant.VCExpire)
                    .build();
        }
        // 验证码是否正确
        if (!loginByVCEntity.getVC().equals(isExistVC)) {
            return LoginResEntity.builder()
                    .isSuccess(false)
                    .message(LoginConstant.VCWrong)
                    .build();
        }
        User existUser = userDao.getUserByUserEmail(loginByVCEntity.getUserEmail());
        // 用户是否存在
        if (existUser != null) {
            // 黑名单
            if(existUser.getIsBlack() == 1) {
                return LoginResEntity.builder()
                        .isSuccess(false)
                        .message(LoginConstant.Black)
                        .build();
            }
            // 删除验证码
            redissonClient.getBucket(key).delete();
            return LoginResEntity.builder()
                    .userId(existUser.getUserId())
                    .isSuccess(true)
                    .message(LoginConstant.Success)
                    .build();
        }
        log.info("邮箱未注册过:{}", loginByVCEntity.getUserEmail());
        //TODO 需要修改，已经在数据库设置唯一约束，但需要新的生成算法，防止userId重复
        String userId = RandomUtil.randomNumbers(9);
        String password = RandomUtil.randomString(9);
        User newUser = User.builder()
                .userId(userId)
                .userName(userId)
                .password(agronUtil.hashPassword(password))
                .userEmail(loginByVCEntity.getUserEmail())
                .build();
        int insert = userDao.insert(newUser);
        if (insert == 1) {
            //TODO 之后改成异步发送邮件
            String content = javaMailUtil.getSendWelContent(userId, userId, password);
            javaMailUtil.sendMimeMessage(loginByVCEntity.getUserEmail(), MailConstant.WelTitle, content, true);
        }
        // 删除验证码
        redissonClient.getBucket(key).delete();
        return LoginResEntity.builder()
                .isSuccess(insert == 1)
                .userId(userId)
                .message(insert == 1 ? RegisterConstant.RegisterSuccess : RegisterConstant.UnknownWrong)
                .build();
    }

}
