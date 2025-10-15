package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.trade.adapter.event.OrderPaySuccessMessageEvent;
import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;
import chat.platform.plus.domain.trade.model.valobj.GoodsTypeEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.domain.trade.service.deliver.DeliverService;
import chat.platform.plus.infrastructure.dao.GoodsDao;
import chat.platform.plus.infrastructure.dao.PayOrderDao;
import chat.platform.plus.infrastructure.dao.po.Goods;
import chat.platform.plus.infrastructure.dao.po.PayOrder;
import chat.platform.plus.infrastructure.event.EventPublisher;
import chat.platform.plus.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TradeRepositoryImpl implements TradeRepository {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private PayOrderDao payOrderDao;

    @Resource
    private Map<String, DeliverService> deliverServiceMap;

    @Resource
    private EventPublisher eventPublisher;

    @Resource
    private OrderPaySuccessMessageEvent orderPaySuccessMessageEvent;

    @Override
    public List<GoodsEntity> getGoodsList() {
        List<Goods> goodsList = goodsDao.getGoodsList();
        if (goodsList == null) {
            return null;
        }
        List<GoodsEntity> goodsEntityList = new ArrayList<>();
        for (Goods goods : goodsList) {
            goodsEntityList.add(GoodsEntity.builder()
                    .goodsId(goods.getGoodsId())
                    .goodsName(goods.getGoodsName())
                    .goodsPrice(goods.getGoodsPrice())
                    .build());
        }
        return goodsEntityList;
    }

    @Override
    public GoodsDetailEntity getGoodsByID(String goodsId) {
        Goods goods = goodsDao.getGoodsByID(goodsId);
        if (goods == null) {
            return null;
        }
        return GoodsDetailEntity.builder()
                .goodsId(goodsId)
                .goodsName(goods.getGoodsName())
                .goodsDesc(goods.getGoodsDesc())
                .goodsPrice(goods.getGoodsPrice())
                .goodsType(GoodsTypeEnum.get(goods.getGoodsType()))
                .build();
    }

    @Override
    public PayOrderEntity getUnPaidOrder(String userId, String goodsId) throws Exception {
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setUserId(userId);
        payOrderReq.setGoodsId(goodsId);
        PayOrder payOrder = payOrderDao.getUnpaidOrder(payOrderReq);
        if (payOrder == null) {
            return null;
        }
        return PayOrderEntity.builder()
                .userId(payOrder.getUserId())
                .goodsId(payOrder.getGoodsId())
                .orderId(payOrder.getOrderId())
                .orderCreateTime(payOrder.getOrderCreateTime())
                .orderPrice(payOrder.getOrderPrice())
                .originalPrice(payOrder.getOrderPrice())
                .orderTypesEnum(OrderTypesEnum.get(payOrder.getOrderType()))
                .deductionPrice(payOrder.getDeductionPrice())
                .payPrice(payOrder.getPayPrice())
                .payUrl(payOrder.getPayUrl())
                .orderStatusEnum(OrderStatusEnum.get(payOrder.getStatus()))
                .build();
    }

    @Override
    public void savePrePayOrder(PrePayOrderEntity prePayOrderEntity) {
        payOrderDao.insert(PayOrder.builder()
                        .userId(prePayOrderEntity.getUserId())
                        .goodsId(prePayOrderEntity.getGoodsId())
                        .orderId(prePayOrderEntity.getOrderId())
                        .orderCreateTime(prePayOrderEntity.getOrderCreateTime())
                        .orderPrice(prePayOrderEntity.getOrderPrice())
                        .orderType(prePayOrderEntity.getOrderTypesEnum().getType())
                        .status(prePayOrderEntity.getOrderStatusEnum().getStatus())
                        .build());
    }

    @Override
    public List<String> getUnNotifyOrderIdList() {
        List<String> orderIdList = payOrderDao.getUnNotifyOrderIdList();
        return orderIdList == null || orderIdList.isEmpty() ? null : orderIdList;
    }

    @Override
    public void updateOrderStatusPaySuccess(String orderId, Date payTime) throws Exception {
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setPayTime(payTime);
        Integer updateCount = payOrderDao.updateOrderStatusPaySuccess(payOrderReq);
        if (updateCount != 1) {
            throw new Exception("更新记录为0");
        }
    }

    @Override
    public List<String> getTimeOutOrderIdList() {
        List<String> orderIdList = payOrderDao.getTimeOutOrderIdList();
        return orderIdList == null || orderIdList.isEmpty() ? null : orderIdList;
    }

    @Override
    public void updateOrderStatusClose(String orderId) {
        payOrderDao.updateOrderStatusClose(orderId);
    }

    @Override
    public PayOrderEntity getUnPaidOrder(String orderId) throws Exception {
        PayOrder payOrder = payOrderDao.getUnPaidOrder(orderId);
        if (payOrder == null) {
            return null;
        }
        return PayOrderEntity.builder()
                .userId(payOrder.getUserId())
                .goodsId(payOrder.getGoodsId())
                .orderId(payOrder.getOrderId())
                .orderCreateTime(payOrder.getCreateTime())
                .orderPrice(payOrder.getOrderPrice())
                .orderTypesEnum(OrderTypesEnum.get(payOrder.getOrderType()))
                .orderStatusEnum(OrderStatusEnum.get(payOrder.getStatus()))
                .build();
    }

    @Override
    public void updateOrderStatusPayWait(String orderId, String payUrl) throws Exception {
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setPayUrl(payUrl);
        Integer updateCount = payOrderDao.updateOrderStatusPayWait(payOrderReq);
        if (updateCount != 1) {
            throw new Exception("更新记录为0");
        }
    }

    @Override
    public void updateOrderStatusTeamComplete(List<String> outTradeNoList) throws Exception {
        Integer updateCount = payOrderDao.updateOrderStatusTeamComplete(outTradeNoList);
        if (updateCount != outTradeNoList.size()) {
            throw new Exception("更新记录为0");
        }
    }

    @Override
    @Transactional(timeout = 500)
    public void deliverGoods(String orderId) throws Exception {
        // 获取未发货订单
        PayOrder payOrder = payOrderDao.getUnDeliverGoodsOrder(orderId);
        if (payOrder != null) {
            Goods goods = goodsDao.getGoodsByID(payOrder.getGoodsId());
            // 枚举策略模式处理商品发货
            GoodsTypeEnum goodsTypeEnum = GoodsTypeEnum.get(goods.getGoodsType());
            DeliverService deliverService = deliverServiceMap.get(goodsTypeEnum.getDeliverStrategy());
            deliverService.deliver(payOrder.getUserId(), goods.getGoodsExpr());
            // 更新订单状态为交易完成
            Integer updateCount = payOrderDao.updateOrderStatusDealDone(orderId);
            if (updateCount != 1) {
                throw new Exception("更新记录为0");
            }
        }
    }

    @Override
    public void deliverGoods(List<String> orderIdList) throws Exception {
        for (String orderId : orderIdList) {
            this.deliverGoods(orderId);
        }
    }

    @Override
    public void updateOrderPrice(String orderId, BigDecimal deductionPrice, BigDecimal payPrice) throws Exception {
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setDeductionPrice(deductionPrice);
        payOrderReq.setPayPrice(payPrice);
        Integer updateCount = payOrderDao.updateOrderPrice(payOrderReq);
        if (updateCount != 1) {
            throw new Exception("更新记录为0");
        }
    }

    @Override
    public void settle(String orderId, Date orderPayTime) throws Exception {
        PayOrderEntity payOrderEntity = this.getUnPaidOrder(orderId);
        if (payOrderEntity != null && payOrderEntity.getOrderStatusEnum().equals(OrderStatusEnum.PAY_WAIT)) {
            log.info("订单状态为待支付，更新为已支付，订单ID：{}", orderId);
            this.updateOrderStatusPaySuccess(orderId, orderPayTime);
            // MQ发送消息
            BaseEvent.EventMessage<OrderPaySuccessMessageEvent.OrderPaySuccessMessage> orderPaySuccessMessageEventMessage = orderPaySuccessMessageEvent.buildEventMessage(
                    OrderPaySuccessMessageEvent.OrderPaySuccessMessage.builder()
                            .userId(payOrderEntity.getUserId())
                            .orderId(orderId)
                            .orderPayTime(orderPayTime)
                            .orderTypesEnum(payOrderEntity.getOrderTypesEnum())
                            .build()
            );
            OrderPaySuccessMessageEvent.OrderPaySuccessMessage orderPaySuccessMessage = orderPaySuccessMessageEventMessage.getData();
            eventPublisher.publish(orderPaySuccessMessageEvent.topic(), JSON.toJSONString(orderPaySuccessMessage));
        }
    }

}
