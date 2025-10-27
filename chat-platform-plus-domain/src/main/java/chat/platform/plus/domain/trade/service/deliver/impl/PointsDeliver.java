package chat.platform.plus.domain.trade.service.deliver.impl;

import chat.platform.plus.domain.trade.adapter.repository.DeliverRepository;
import chat.platform.plus.domain.trade.service.deliver.DeliverService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("points")
public class PointsDeliver implements DeliverService {

    @Resource
    private DeliverRepository deliverRepository;

    @Override
    public void deliver(String userId, Integer goodsExpr) throws Exception {
        log.info("积分发货，用户ID：{}，积分点数：{}", userId, goodsExpr);
        deliverRepository.deliverPoints(userId, goodsExpr);
    }

}
