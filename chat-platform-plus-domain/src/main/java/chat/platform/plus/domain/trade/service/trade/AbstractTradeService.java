package chat.platform.plus.domain.trade.service.trade;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.*;
import chat.platform.plus.domain.trade.model.valobj.GoodsTypeEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.domain.trade.model.valobj.RefundOrderStatusEnum;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import ltzf.payments.nativepay.model.refund.RefundResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付抽象类
 */
@Slf4j
public abstract class AbstractTradeService implements TradeService {

    @Resource
    protected TradePort tradePort;

    @Resource
    protected TradeRepository tradeRepository;

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
                GroupBuyLockOrderEntity groupBuyLockOrderEntity = this.lockOrder(shopCartEntity.getUserId(), shopCartEntity.getTeamId(), goodsDetailEntity.getGoodsId(), shopCartEntity.getActivityId(), unPaidPayOrderEntity.getOrderId(), shopCartEntity.getInviteId());
                prepayResponse = doPrePayOrder(unPaidPayOrderEntity.getOrderId(), unPaidPayOrderEntity.getOrderPrice(), goodsDetailEntity.getGoodsName(), groupBuyLockOrderEntity);
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
        GroupBuyLockOrderEntity groupBuyLockOrderEntity = GroupBuyLockOrderEntity.builder()
                .originalPrice(prePayOrderEntity.getOrderPrice())
                .deductionPrice(BigDecimal.ZERO)
                .payPrice(prePayOrderEntity.getOrderPrice())
                .build();
        if (OrderTypesEnum.GROUPBUY.equals(shopCartEntity.getOrderTypesEnum())) {
            log.info("进行拼团锁单，用户ID：{}，商品ID：{}，活动ID：{}", shopCartEntity.getUserId(), shopCartEntity.getGoodsId(), shopCartEntity.getActivityId());
            groupBuyLockOrderEntity = this.lockOrder(shopCartEntity.getUserId(), shopCartEntity.getTeamId(), shopCartEntity.getGoodsId(), shopCartEntity.getActivityId(), orderId, shopCartEntity.getInviteId());
        }
        log.info("订单价格计算结果：{}", JSON.toJSONString(groupBuyLockOrderEntity));

        // 更新折扣价格和支付价格
        tradeRepository.updateOrderPrice(orderId, groupBuyLockOrderEntity.getDeductionPrice(), groupBuyLockOrderEntity.getPayPrice());

        // 蓝兔支付 - 生成收款码
        prepayResponse = doPrePayOrder(prePayOrderEntity.getOrderId(), prePayOrderEntity.getOrderPrice(), goodsDetailEntity.getGoodsName(), groupBuyLockOrderEntity);
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
                    .deductionPrice(groupBuyLockOrderEntity.getDeductionPrice())
                    .payPrice(groupBuyLockOrderEntity.getPayPrice())
                    .payUrl(prepayResponse.getData().getQrcodeUrl())
                    .orderStatusEnum(OrderStatusEnum.PAY_WAIT)
                    .build();
        } else {
            throw new Exception("生成收款码异常：" + prepayResponse.getMsg());
        }
    }

    @Override
    public RefundResEntity createRefundOrder(PreRefundOrderEntity preRefundOrderEntity) throws Exception {
        // 查询需要退单的订单是否存在
        PayOrderEntity payOrderEntity = tradeRepository.getPayOrder(preRefundOrderEntity.getUserId(), preRefundOrderEntity.getOrderId());
        if (payOrderEntity == null) {
            throw new Exception("不存在的订单");
        }

        // 订单退款情况 - 防止重复退款
        RefundOrderEntity isRefundOrderEntity = tradeRepository.getRefundOrder(preRefundOrderEntity.getUserId(), preRefundOrderEntity.getOrderId());
        if (isRefundOrderEntity != null && isRefundOrderEntity.getRefundOrderStatusEnum().equals(RefundOrderStatusEnum.SUCCESS)) {
            log.info("订单退款完成，用户ID：{}，支付订单ID：{}，退单订单ID：{}，退款时间：{}",
                    isRefundOrderEntity.getUserId(), isRefundOrderEntity.getPayOrderId(), isRefundOrderEntity.getRefundOrderId(), isRefundOrderEntity.getRefundTime());
            return RefundResEntity.builder()
                    .userId(preRefundOrderEntity.getUserId())
                    .orderId(preRefundOrderEntity.getOrderId())
                    .refundOrderStatusEnum(RefundOrderStatusEnum.SUCCESS)
                    .info("请勿重复退款")
                    .build();
        }

        // 商品是否可退款
        GoodsDetailEntity goodsDetailEntity = tradeRepository.getGoodsByID(payOrderEntity.getGoodsId());
        if (!goodsDetailEntity.getGoodsType().equals(GoodsTypeEnum.OTHER)) {
            log.info("该商品不支持退款，商品名称：{}，商品类型：{}", goodsDetailEntity.getGoodsName(), goodsDetailEntity.getGoodsType().getInfo());
            return RefundResEntity.builder()
                    .userId(preRefundOrderEntity.getUserId())
                    .orderId(preRefundOrderEntity.getOrderId())
                    .refundOrderStatusEnum(RefundOrderStatusEnum.FAIL)
                    .info("订单购买的商品不支持退款")
                    .build();
        }

        // 首次进行退款 - 创建退单订单
        log.info("正常创建退单订单，用户ID：{}，支付订单ID：{}", preRefundOrderEntity.getUserId(), preRefundOrderEntity.getOrderId());
        String refundOrderId = PreRefundOrderEntity.createRefundOrderId();
        RefundOrderEntity refundOrderEntity = RefundOrderEntity.builder()
                .userId(preRefundOrderEntity.getUserId())
                .payOrderId(payOrderEntity.getOrderId())
                .refundOrderId(refundOrderId)
                .refundOrderPrice(payOrderEntity.getPayPrice())
                .refundOrderCreateTime(new Date())
                .refundOrderStatusEnum(RefundOrderStatusEnum.INIT)
                .orderTypesEnum(payOrderEntity.getOrderTypesEnum())
                .build();
        this.saveRefundOrder(refundOrderEntity);

        // 拼团购买 - 请求拼团服务进行退单
        if (payOrderEntity.getOrderTypesEnum().equals(OrderTypesEnum.GROUPBUY)) {
            log.info("进行拼团退单，用户ID：{}，拼团ID：{}", preRefundOrderEntity.getUserId(), preRefundOrderEntity.getOrderId());
            this.refundOrder(preRefundOrderEntity.getUserId(), preRefundOrderEntity.getOrderId());
        }
        // 蓝兔支付退单
        RefundResponse refundResponse = doRefundOrder(payOrderEntity.getOrderStatusEnum(), preRefundOrderEntity.getOrderId(), refundOrderId, payOrderEntity.getPayPrice(), preRefundOrderEntity.getDesc());
        log.info("退单结果：{}", refundResponse);

        return RefundResEntity.builder()
                .userId(preRefundOrderEntity.getUserId())
                .orderId(preRefundOrderEntity.getOrderId())
                .refundOrderId(refundOrderId)
                .refundOrderCreateTime(refundOrderEntity.getRefundOrderCreateTime())
                .refundOrderStatusEnum(RefundOrderStatusEnum.INIT)
                .info("退款中")
                .build();
    }

    /**
     * 保存预支付订单
     * @param prePayOrderEntity 预支付订单实体
     * @return
     */
    protected abstract void savePrePayOrder(PrePayOrderEntity prePayOrderEntity);

    /**
     * 保存退单订单
     * @param refundOrderEntity
     */
    protected abstract void saveRefundOrder(RefundOrderEntity refundOrderEntity);

    /**
     * 锁单
     * @param userId 用户ID
     * @param teamId 组队ID
     * @param goodsId 商品ID
     * @param activityId 活动ID
     * @param orderId 订单ID
     * @param inviteId 邀请码
     * @return
     */
    protected abstract GroupBuyLockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId, String inviteId);

    /**
     * 退单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return
     */
    protected abstract void refundOrder(String userId, String orderId);

    /**
     * 蓝兔支付 - 生成收款码
     * @param orderId 订单ID
     * @param orderPrice 金额
     * @param goodsName 商品名称
     * @return
     * @throws IOException
     */
    protected PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException {
        return tradePort.doPrePayOrder(orderId, orderPrice, goodsName, null);
    }

    /**
     * 拼团营销后蓝兔支付 - 生成收款码
     * @param orderId 订单ID
     * @param orderPrice 订单价格
     * @param goodsName 商品名称
     * @param groupBuyLockOrderEntity 锁单结果实体
     * @return
     * @throws IOException
     */
    protected PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, GroupBuyLockOrderEntity groupBuyLockOrderEntity) throws IOException {
        return tradePort.doPrePayOrder(orderId, orderPrice, goodsName, groupBuyLockOrderEntity);
    }

    /**
     * 蓝兔支付 - 退款
     * @param orderId 支付订单ID
     * @param refundOrderId 退单订单ID
     * @param refundOrderPrice 退单金额
     * @param refundDesc 退单描述
     * @return
     */
    protected RefundResponse doRefundOrder(OrderStatusEnum orderStatusEnum, String orderId, String refundOrderId, BigDecimal refundOrderPrice, String refundDesc) throws Exception {
        // 订单符合已支付相关状态则可退款
        if (orderStatusEnum.equals(OrderStatusEnum.PAY_SUCCESS) || orderStatusEnum.equals(OrderStatusEnum.DEAL_DONE) || orderStatusEnum.equals(OrderStatusEnum.GROUP_BUY_COMPLETE)) {
            log.info("当前订单状态：{}，调用蓝兔支付进行退款，订单ID：{}", orderStatusEnum.getDesc(), orderId);
            RefundResponse refundResponse = tradePort.doRefundOrder(orderId, refundOrderId, refundOrderPrice, refundDesc);
            log.info("订单ID：{}，退单结果：{}", orderId, JSON.toJSONString(refundResponse));
            if (refundResponse.getCode() != 0) {
                throw new Exception("蓝兔支付退款失败：" + refundResponse.getMsg());
            }
            return refundResponse;
        } else {
            log.info("当前订单状态：{}，无需调用蓝兔支付进行退款，订单ID：{}", orderStatusEnum.getDesc(), orderId);
            return null;
        }
    }

}
