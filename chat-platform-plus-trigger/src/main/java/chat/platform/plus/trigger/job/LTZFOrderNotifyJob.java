package chat.platform.plus.trigger.job;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.types.common.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.util.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.common.CodeEnum;
import ltzf.common.PayStatusEnum;
import ltzf.payments.nativepay.model.order.OrderResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单支付回调任务
 */
@Slf4j
@Service
public class LTZFOrderNotifyJob {

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private TradePort tradePort;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0/10 * * * * ?")
    private void exec() {
        RLock lock = redissonClient.getLock(Constants.OrderNotifyJobLock);
        try {
            boolean getLock = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!getLock) {
                log.info("获取锁失败：{}，此时有其他应用在执行订单支付回调任务，等待", Constants.OrderNotifyJobLock);
                return;
            }
            log.info("获取锁成功：{}，订单支付回调任务开始", Constants.OrderNotifyJobLock);
            List<String> orderIdList = tradeRepository.getUnNotifyOrderIdList();
            if (orderIdList == null || orderIdList.isEmpty()) {
                log.info("没有需要回调的订单 - 订单支付回调任务结束");
                return;
            }
            for (String orderId : orderIdList) {
                OrderResponse orderResponse = tradePort.getOrderResponse(orderId);
                log.info("订单支付回调任务 - 订单ID：{}，查询订单状态：{}", orderId, JSON.toJSONString(orderResponse));
                // 订单支付成功 - 进行结算
                if (orderResponse.getCode() == Integer.parseInt(CodeEnum.SUCCESS.getCode()) &&
                        orderResponse.getData().getPayStatus().equals(PayStatusEnum.ISPAID.getStatus())) {
                    log.info("订单ID：{} 已支付，进行后续结算", orderId);
                    // 结算
                    tradeRepository.settle(orderId, DateUtils.parseDate(orderResponse.getData().getSuccessTime(), "yyyy-MM-dd HH:mm:ss"));
                }
            }
            log.info("订单支付回调任务结束");
        } catch (Exception e) {
            log.info("订单支付回调任务执行异常", e);
        } finally {
            // 检查锁是否被任何线程持有以及是否被当前线程持有 - 释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
