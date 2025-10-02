package chat.platform.plus.domain.trade.service.pay;

import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.ShopCartEntity;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付抽象类
 */
@Slf4j
public abstract class AbstractTradeService implements TradeService {

    @Resource
    private TradeRepository tradeRepository;

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception {
        // 用户是否存在相同商品的待支付订单
        PayOrderEntity unPaidPayOrderEntity = tradeRepository.getUnPaidOrder(shopCartEntity.getUserId(), shopCartEntity.getGoodsId());
        if (unPaidPayOrderEntity != null && unPaidPayOrderEntity.getOrderStatusEnum().equals(OrderStatusEnum.PAY_WAIT)) {
            log.info("存在相同商品的待支付订单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
            return unPaidPayOrderEntity;
        } else if (unPaidPayOrderEntity != null && unPaidPayOrderEntity.getOrderStatusEnum().equals(OrderStatusEnum.CREATE)) {
            // 用户是否存在掉单
            log.info("存在掉单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
            GoodsDetailEntity goodsDetailEntity = tradeRepository.getGoodsByID(unPaidPayOrderEntity.getGoodsId());
            if (goodsDetailEntity == null) {
                throw new Exception("不存在的商品");
            }
            // 蓝兔支付 - 生成收款码
            PrepayResponse prepayResponse = doPrePayOrder(unPaidPayOrderEntity.getOrderId(), unPaidPayOrderEntity.getOrderPrice(), goodsDetailEntity.getGoodsName());
            if (prepayResponse.getCode() == 0) {
                // 生成收款码成功 - 订单状态更新为待支付
                log.info("生成收款码完成，更新订单状态为待支付，订单ID：{}", unPaidPayOrderEntity.getOrderId());
                tradeRepository.updateOrderStatusPayWait(unPaidPayOrderEntity.getOrderId(), prepayResponse.getData().getQrcodeUrl());
                return PayOrderEntity.builder()
                        .userId(unPaidPayOrderEntity.getUserId())
                        .goodsId(unPaidPayOrderEntity.getGoodsId())
                        .orderId(unPaidPayOrderEntity.getOrderId())
                        .orderCreateTime(unPaidPayOrderEntity.getOrderCreateTime())
                        .orderPrice(unPaidPayOrderEntity.getOrderPrice())
                        .payUrl(prepayResponse.getData().getQrcodeUrl())
                        .orderStatusEnum(OrderStatusEnum.PAY_WAIT)
                        .build();
            } else {
                throw new Exception("生成收款码异常：" + prepayResponse.getMsg());
            }
        }
        log.info("正常创建订单，用户ID：{}，商品ID：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId());
        // 查询商品信息
        GoodsDetailEntity goodsDetailEntity = tradeRepository.getGoodsByID(shopCartEntity.getGoodsId());
        if (goodsDetailEntity == null) {
            throw new Exception("不存在的商品");
        }
        String orderId = PrePayOrderEntity.createOrderId();
        PrePayOrderEntity prePayOrderEntity = PrePayOrderEntity.builder()
                .userId(shopCartEntity.getUserId())
                .goodsId(goodsDetailEntity.getGoodsId())
                .orderId(orderId)
                .orderCreateTime(new Date())
                .orderPrice(goodsDetailEntity.getGoodsPrice())
                .orderStatusEnum(OrderStatusEnum.CREATE)
                .build();
        // 保存预支付订单
        savePrePayOrder(prePayOrderEntity);
        // 蓝兔支付 - 生成收款码
        PrepayResponse prepayResponse = doPrePayOrder(prePayOrderEntity.getOrderId(), prePayOrderEntity.getOrderPrice(), goodsDetailEntity.getGoodsName());
        log.info("订单ID：{}，蓝兔支付 - 生成收款码响应：{}", orderId, JSON.toJSONString(prepayResponse));
        if (prepayResponse.getCode() == 0) {
            // 生成收款码成功 - 订单状态更新为待支付
            log.info("生成收款码成功，更新订单状态为待支付，订单ID：{}", orderId);
            tradeRepository.updateOrderStatusPayWait(orderId, prepayResponse.getData().getQrcodeUrl());
            return PayOrderEntity.builder()
                    .userId(prePayOrderEntity.getUserId())
                    .goodsId(prePayOrderEntity.getGoodsId())
                    .orderId(orderId)
                    .orderCreateTime(prePayOrderEntity.getOrderCreateTime())
                    .orderPrice(prePayOrderEntity.getOrderPrice())
                    .payUrl(prepayResponse.getData().getQrcodeUrl())
                    .orderStatusEnum(OrderStatusEnum.PAY_WAIT)
                    .build();
        } else {
            throw new Exception("生成收款码异常");
        }
    }

    /**
     * 保存预支付订单
     * @param prePayOrderEntity 预支付订单实体
     * @return
     */
    protected abstract void savePrePayOrder(PrePayOrderEntity prePayOrderEntity);

    /**
     * 蓝兔支付 - 生成收款码
     * @return
     */
    protected abstract PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException;

}
