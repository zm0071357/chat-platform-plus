package chat.platform.plus.domain.trade.service.pay;

import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.*;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
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
        // 蓝兔支付 - 生成收款码
        PrepayResponse prepayResponse;
        // 用户是否存在相同商品的待支付订单
        PayOrderEntity unPaidPayOrderEntity = tradeRepository.getUnPaidOrder(shopCartEntity.getUserId(), shopCartEntity.getGoodsId());
        if (unPaidPayOrderEntity != null && unPaidPayOrderEntity.getOrderStatusEnum().equals(OrderStatusEnum.PAY_WAIT)) {
            log.info("存在相同商品的待支付订单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
            return unPaidPayOrderEntity;
        } else if (unPaidPayOrderEntity != null && unPaidPayOrderEntity.getOrderStatusEnum().equals(OrderStatusEnum.CREATE)) {
            // 用户是否存在掉单
            log.info("用户存在掉单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
            GoodsDetailEntity goodsDetailEntity = tradeRepository.getGoodsByID(unPaidPayOrderEntity.getGoodsId());
            if (goodsDetailEntity == null) {
                throw new Exception("不存在的商品");
            }
            // 用户是否在拼团锁单时掉单
            if (OrderTypesEnum.GROUPBUY.equals(unPaidPayOrderEntity.getOrderTypesEnum()) && unPaidPayOrderEntity.getDeductionPrice() == null) {
                log.info("用户存在掉单 - 拼团锁单未成功掉单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
                // 锁单
                LockOrderEntity lockOrderEntity = this.lockOrder(shopCartEntity.getUserId(), shopCartEntity.getTeamId(), goodsDetailEntity.getGoodsId(), shopCartEntity.getActivityId(), unPaidPayOrderEntity.getOrderId());
                prepayResponse = doPrePayOrder(unPaidPayOrderEntity.getOrderId(), unPaidPayOrderEntity.getOrderPrice(), goodsDetailEntity.getGoodsName(), lockOrderEntity);
            } else if (OrderTypesEnum.GROUPBUY.equals(unPaidPayOrderEntity.getOrderTypesEnum())) {
                log.info("用户存在掉单 - 拼团锁单成功后掉单，生成蓝兔支付订单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
                prepayResponse = doPrePayOrder(unPaidPayOrderEntity.getOrderId(), unPaidPayOrderEntity.getPayPrice(), goodsDetailEntity.getGoodsName());
            } else {
                log.info("用户存在掉单 - 直接购买掉单，生成蓝兔支付订单，用户ID：{}，商品ID：{}，订单信息：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), JSON.toJSONString(unPaidPayOrderEntity));
                prepayResponse = doPrePayOrder(unPaidPayOrderEntity.getOrderId(), unPaidPayOrderEntity.getPayPrice(), goodsDetailEntity.getGoodsName());
            }

            // 生成收款码成功 - 订单状态更新为待支付
            if (prepayResponse.getCode() == 0) {
                log.info("生成收款码完成，更新订单状态为待支付，订单ID：{}", unPaidPayOrderEntity.getOrderId());
                tradeRepository.updateOrderStatusPayWait(unPaidPayOrderEntity.getOrderId(), prepayResponse.getData().getQrcodeUrl());
                return PayOrderEntity.builder()
                        .userId(unPaidPayOrderEntity.getUserId())
                        .goodsId(unPaidPayOrderEntity.getGoodsId())
                        .orderId(unPaidPayOrderEntity.getOrderId())
                        .orderCreateTime(unPaidPayOrderEntity.getOrderCreateTime())
                        .orderPrice(unPaidPayOrderEntity.getOrderPrice())
                        .originalPrice(unPaidPayOrderEntity.getOriginalPrice())
                        .deductionPrice(unPaidPayOrderEntity.getDeductionPrice())
                        .payPrice(unPaidPayOrderEntity.getPayPrice())
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

        // 构建预支付订单
        String orderId = PrePayOrderEntity.createOrderId();
        PrePayOrderEntity prePayOrderEntity = PrePayOrderEntity.builder()
                .userId(shopCartEntity.getUserId())
                .goodsId(goodsDetailEntity.getGoodsId())
                .orderId(orderId)
                .orderCreateTime(new Date())
                .orderPrice(goodsDetailEntity.getGoodsPrice())
                .orderTypesEnum(shopCartEntity.getOrderTypesEnum())
                .orderStatusEnum(OrderStatusEnum.CREATE)
                .build();

        // 保存预支付订单
        this.savePrePayOrder(prePayOrderEntity);

        // 请求锁单
        LockOrderEntity lockOrderEntity = LockOrderEntity.builder()
                .originalPrice(prePayOrderEntity.getOrderPrice())
                .deductionPrice(BigDecimal.ZERO)
                .payPrice(prePayOrderEntity.getOrderPrice())
                .build();
        if (OrderTypesEnum.GROUPBUY.equals(shopCartEntity.getOrderTypesEnum())) {
            log.info("进行拼团锁单，用户ID：{}，商品ID：{}，活动ID：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), shopCartEntity.getActivityId());
            lockOrderEntity = this.lockOrder(shopCartEntity.getUserId(), shopCartEntity.getTeamId(), shopCartEntity.getGoodsId(), shopCartEntity.getActivityId(), orderId);
        }
        log.info("订单价格计算结果：{}", JSON.toJSONString(lockOrderEntity));

        // 更新折扣价格和支付价格
        tradeRepository.updateOrderPrice(orderId, lockOrderEntity.getDeductionPrice(), lockOrderEntity.getPayPrice());

        // 蓝兔支付 - 生成收款码
        prepayResponse = doPrePayOrder(prePayOrderEntity.getOrderId(), prePayOrderEntity.getOrderPrice(), goodsDetailEntity.getGoodsName(), lockOrderEntity);
        log.info("订单ID：{}，蓝兔支付 - 生成收款码响应：{}", orderId, JSON.toJSONString(prepayResponse));
        // 生成收款码成功 - 订单状态更新为待支付
        if (prepayResponse.getCode() == 0) {
            log.info("生成收款码成功，更新订单状态为待支付，订单ID：{}", orderId);
            tradeRepository.updateOrderStatusPayWait(orderId, prepayResponse.getData().getQrcodeUrl());
            return PayOrderEntity.builder()
                    .userId(prePayOrderEntity.getUserId())
                    .goodsId(prePayOrderEntity.getGoodsId())
                    .orderId(orderId)
                    .orderCreateTime(prePayOrderEntity.getOrderCreateTime())
                    .orderPrice(prePayOrderEntity.getOrderPrice())
                    .originalPrice(prePayOrderEntity.getOrderPrice())
                    .deductionPrice(lockOrderEntity.getDeductionPrice())
                    .payPrice(lockOrderEntity.getPayPrice())
                    .payUrl(prepayResponse.getData().getQrcodeUrl())
                    .orderStatusEnum(OrderStatusEnum.PAY_WAIT)
                    .build();
        } else {
            throw new Exception("生成收款码异常：" + prepayResponse.getMsg());
        }
    }

    /**
     * 锁单
     * @param userId 用户ID
     * @param teamId 组队ID
     * @param goodsId 商品ID
     * @param activityId 活动ID
     * @param orderId 订单ID
     * @return
     */
    protected abstract LockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId);

    /**
     * 保存预支付订单
     * @param prePayOrderEntity 预支付订单实体
     * @return
     */
    protected abstract void savePrePayOrder(PrePayOrderEntity prePayOrderEntity);

    /**
     * 蓝兔支付 - 生成收款码
     * @param orderId 订单ID
     * @param orderPrice 金额
     * @param goodsName 商品名称
     * @return
     * @throws IOException
     */
    protected abstract PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException;


    /**
     * 拼团营销调用蓝兔支付 - 生成收款码
     * @param orderId 订单ID
     * @param orderPrice 订单价格
     * @param goodsName 商品名称
     * @param lockOrderEntity 锁单结果实体
     * @return
     * @throws IOException
     */
    protected abstract PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, LockOrderEntity lockOrderEntity) throws IOException;

}
