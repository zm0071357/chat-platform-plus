package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.login.adapter.repository.RegisterRepository;
import chat.platform.plus.domain.login.model.entity.RegisterEntity;
import chat.platform.plus.domain.login.model.entity.RegisterResEntity;
import chat.platform.plus.domain.login.model.entity.VCEntity;
import chat.platform.plus.domain.login.model.valobj.RegisterConstant;
import chat.platform.plus.infrastructure.dao.UserDao;
import chat.platform.plus.domain.login.model.valobj.VCConstant;
import chat.platform.plus.infrastructure.dao.po.User;
import chat.platform.plus.types.common.MailConstant;
import chat.platform.plus.types.utils.AgronUtil;
import chat.platform.plus.types.utils.JavaMailUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class RegisterRepositoryImpl implements RegisterRepository {

    @Resource
    private UserDao userDao;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private JavaMailUtil javaMailUtil;

    @Resource
    private AgronUtil agronUtil;

    @Override
    public RegisterResEntity register(RegisterEntity registerEntity) {
        String key = RegisterEntity.getVCKey(registerEntity.getUserEmail());
        String isExistVC = (String) redissonClient.getBucket(key).get();
        // 验证码是否过期
        if (isExistVC == null) {
            return RegisterResEntity.builder()
                    .isSuccess(false)
                    .message(RegisterConstant.VCExpire)
                    .build();
        }
        // 验证码是否正确
        if (!registerEntity.getVC().equals(isExistVC)) {
            return RegisterResEntity.builder()
                    .isSuccess(false)
                    .message(RegisterConstant.VCWrong)
                    .build();
        }
        // 邮箱是否重复使用
        User existUser = userDao.getUserByUserEmail(registerEntity.getUserEmail());
        if (existUser != null) {
            return RegisterResEntity.builder()
                    .isSuccess(false)
                    .message(RegisterConstant.EmailExist)
                    .build();
        }
        //TODO 需要修改，已经在数据库设置唯一约束，但需要新的生成算法，防止userId重复
        String userId = RandomUtil.randomNumbers(9);
        User newUser = User.builder()
                .userId(userId)
                .userName(registerEntity.getUserName())
                .password(agronUtil.hashPassword(registerEntity.getPassword()))
                .userEmail(registerEntity.getUserEmail())
                .build();
        int insert = userDao.insert(newUser);
        if (insert == 1) {
            //TODO 之后改成异步发送邮件
            String content = javaMailUtil.getSendWelContent(userId, registerEntity.getUserName(), registerEntity.getPassword());
            javaMailUtil.sendMimeMessage(registerEntity.getUserEmail(), MailConstant.WelTitle, content, true);
        }
        // 删除验证码
        redissonClient.getBucket(key).delete();
        return RegisterResEntity.builder()
                .isSuccess(insert == 1)
                .userId(userId)
                .message(insert == 1 ? RegisterConstant.RegisterSuccess : RegisterConstant.UnknownWrong)
                .build();
    }

    @Override
    public VCEntity getVC(String userEmail) {
        // 校验是否已经生成验证码
        String key = VCEntity.getVCKey(userEmail);
        String ifExistVC = (String) redissonClient.getBucket(key).get();
        if (ifExistVC != null) {
            log.info("发送验证码失败:{},{}", userEmail, VCConstant.VCRepeated);
            return VCEntity.builder()
                    .isSuccess(false)
                    .message(VCConstant.VCRepeated)
                    .build();
        }
        // 生成验证码
        String VC = RandomUtil.randomNumbers(VCConstant.VCLength);
        // 发送验证码到用户邮箱
        if (javaMailUtil.sendMimeMessage(userEmail, MailConstant.VCTitle, javaMailUtil.getSendVCContent(VC), true)) {
            log.info("发送验证码成功:{},{}", userEmail, VC);
            // 缓存验证码 - 有效期3分钟
            redissonClient.getBucket(key).set(VC, VCConstant.VCTime, TimeUnit.MINUTES);
            return VCEntity.builder()
                    .isSuccess(true)
                    .VC(VC)
                    .message(VCConstant.VCSuccess)
                    .build();
        }
        log.info("发送验证码失败:{},{}", userEmail, VCConstant.UnknownWrong);
        return VCEntity.builder()
                .isSuccess(false)
                .message(VCConstant.UnknownWrong)
                .build();
    }

}
