package chat.platform.plus.trigger.job;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.service.pay.TradeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.util.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.common.CodeEnum;
import ltzf.common.PayStatusEnum;
import ltzf.payments.nativepay.model.order.OrderResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    @Scheduled(cron = "0/10 * * * * ?")
    private void exec() {
        try {
            log.info("订单支付回调任务开始");
            List<String> orderIdList = tradeRepository.getUnNotifyOrderIdList();
            if (orderIdList == null || orderIdList.isEmpty()) {
                log.info("没有需要回调的订单 - 订单支付回调任务结束");
                return;
            }
            for (String orderId : orderIdList) {
                OrderResponse orderResponse = tradePort.getOrderResponse(orderId);
                log.info("订单支付回调任务 - 订单ID：{}，查询订单状态：{}", orderId, JSON.toJSONString(orderResponse));
                // 订单支付成功 - 更新订单状态和支付时间
                if (orderResponse.getCode() == Integer.parseInt(CodeEnum.SUCCESS.getCode()) &&
                        orderResponse.getData().getPayStatus().equals(PayStatusEnum.ISPAID.getStatus())) {
                    log.info("订单ID：{} 已支付，更新订单状态", orderId);
                    Integer updateCount = tradeRepository.updateOrderStatusPaySuccess(orderId,
                            DateUtils.parseDate(orderResponse.getData().getSuccessTime(), "yyyy-MM-dd HH:mm:ss"));
                    if (updateCount != 1) {
                        throw new Exception("订单状态更新失败");
                    }
                    // TODO：根据商品类型做不同处理
                }
            }
            log.info("订单支付回调任务结束");
        } catch (Exception e) {
            log.info("订单支付回调任务执行异常", e);
        }
    }
}
