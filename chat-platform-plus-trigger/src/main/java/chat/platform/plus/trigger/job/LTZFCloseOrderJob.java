package chat.platform.plus.trigger.job;

import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.types.common.Constants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单超时关单任务
 */
@Slf4j
@Service
public class LTZFCloseOrderJob {

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0/10 * * * * ?")
    private void exec() {
        RLock lock = redissonClient.getLock(Constants.CloseOrderJobLock);
        try {
            boolean getLock = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!getLock) {
                log.info("获取锁失败：{}，此时有其他应用在执行订单超时关单任务，等待", Constants.CloseOrderJobLock);
                return;
            }
            log.info("获取锁成功：{}，订单超时关单任务开始", Constants.CloseOrderJobLock);
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
        } finally {
            // 检查锁是否被任何线程持有以及是否被当前线程持有 - 释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
