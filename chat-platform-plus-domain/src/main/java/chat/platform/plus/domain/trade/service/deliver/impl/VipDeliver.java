package chat.platform.plus.domain.trade.service.deliver.impl;

import chat.platform.plus.domain.trade.adapter.repository.DeliverRepository;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.service.deliver.DeliverService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("vip")
public class VipDeliver implements DeliverService {

    @Resource
    private DeliverRepository deliverRepository;

    @Override
    public void deliver(String userId, Integer goodsExpr) throws Exception {
        log.info("VIP特权发货，用户ID：{}，VIP特权标识：{}", userId, goodsExpr);
        deliverRepository.deliverVip(userId, goodsExpr);
    }
}
