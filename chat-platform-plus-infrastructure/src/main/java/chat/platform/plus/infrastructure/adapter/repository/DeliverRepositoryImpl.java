package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.trade.adapter.repository.DeliverRepository;
import chat.platform.plus.infrastructure.dao.UserDao;
import chat.platform.plus.infrastructure.dao.po.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeliverRepositoryImpl implements DeliverRepository {

    @Resource
    private UserDao userDao;

    @Override
    public void deliverCount(String userId, Integer goodsExpr) throws Exception {
        User userReq = new User();
        userReq.setUserId(userId);
        userReq.setInvokeCount(goodsExpr);
        Integer updateCount = userDao.updateInvokeCount(userReq);
        if (updateCount != 1) {
            throw new Exception("更新记录为0");
        }
    }

    @Override
    public void deliverVip(String userId, Integer goodsExpr) throws Exception {
        User userReq = new User();
        userReq.setUserId(userId);
        userReq.setIsVip(goodsExpr);
        Integer updateCount = userDao.updateVip(userReq);;
        if (updateCount != 1) {
            throw new Exception("更新记录为0");
        }
    }

}
