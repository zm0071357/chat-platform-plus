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
import ltzf.payments.nativepay.model.order.RefundOrderResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单退款回调任务
 */
@Slf4j
@Service
public class LZTFRefundOrderNotifyJob {

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private TradePort tradePort;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0/30 * * * * ?")
    private void exec() {
        RLock lock = redissonClient.getLock(Constants.RefundOrderNotifyJobLock);
        try {
            boolean getLock = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!getLock) {
                log.info("获取锁失败：{}，此时有其他应用在执行订单退款回调任务，等待", Constants.RefundOrderNotifyJobLock);
                return;
            }
            log.info("获取锁成功：{}，订单退款回调任务开始", Constants.RefundOrderNotifyJobLock);
            List<String> refundOrderIdList = tradeRepository.getUnNotifyRefundOrderIdList();
            if (refundOrderIdList == null || refundOrderIdList.isEmpty()) {
                log.info("没有需要回调的订单 - 订单退款回调任务结束");
                return;
            }
            for (String refundOrderId : refundOrderIdList) {
                RefundOrderResponse refundOrderResponse = tradePort.getRefundOrderResponse(refundOrderId);
                log.info("订单退款回调任务 - 订单ID：{}，查询订单状态：{}", refundOrderId, JSON.toJSONString(refundOrderResponse));
                // 订单退款成功
                if (refundOrderResponse.getCode() == Integer.parseInt(CodeEnum.SUCCESS.getCode()) && refundOrderResponse.getData().getRefundStatus() == 1) {
                    log.info("订单ID：{} 已退款，更新订单状态为已退款", refundOrderId);
                    // 退单结算
                    tradeRepository.orderRefundSuccess(refundOrderId, DateUtils.parseDate(refundOrderResponse.getData().getSuccessTime(), "yyyy-MM-dd HH:mm:ss"));
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
