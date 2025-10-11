package chat.platform.plus.domain.trade.service.deliver.impl;

import chat.platform.plus.domain.trade.service.deliver.DeliverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("other")
public class OtherDeliver implements DeliverService {

    @Override
    public void deliver(String userId, Integer goodsExpr) {
        log.info("其他商品发货，用户ID：{}", userId);
    }
}
