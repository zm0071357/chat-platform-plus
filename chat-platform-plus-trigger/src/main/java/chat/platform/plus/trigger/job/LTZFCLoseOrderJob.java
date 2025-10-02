package chat.platform.plus.trigger.job;

import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单超时关单任务
 */
@Slf4j
@Service
public class LTZFCLoseOrderJob {

    @Resource
    private TradeRepository tradeRepository;

    @Scheduled(cron = "0/10 * * * * ?")
    private void exec() {
        try {
            log.info("订单超时关单任务开始");
            List<String> orderIdList = tradeRepository.getTimeOutOrderIdList();
            if (orderIdList == null || orderIdList.isEmpty()) {
                log.info("没有需要关单的订单 - 订单超时关单任务结束");
                return;
            }
            for (String orderId : orderIdList) {
                log.info("订单超时关单任务 - 订单ID：{}", orderId);
                tradeRepository.updateOrderStatusClose(orderId);
            }
            log.info("订单超时关单任务结束");
        } catch (Exception e) {
            log.info("订单超时关单任务执行异常", e);
        }
    }
}
