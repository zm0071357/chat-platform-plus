package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;
import chat.platform.plus.domain.trade.model.valobj.GoodsTypeEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.infrastructure.dao.GoodsDao;
import chat.platform.plus.infrastructure.dao.PayOrderDao;
import chat.platform.plus.infrastructure.dao.po.Goods;
import chat.platform.plus.infrastructure.dao.po.PayOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class TradeRepositoryImpl implements TradeRepository {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private PayOrderDao payOrderDao;

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
                        .status(prePayOrderEntity.getOrderStatusEnum().getStatus())
                        .build());
    }

    @Override
    public List<String> getUnNotifyOrderIdList() {
        List<String> orderIdList = payOrderDao.getUnNotifyOrderIdList();
        return orderIdList == null || orderIdList.isEmpty() ? null : orderIdList;
    }

    @Override
    public Integer updateOrderStatusPaySuccess(String orderId, Date payTime) throws Exception {
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setPayTime(payTime);
        Integer updateCount = payOrderDao.updateOrderStatusPaySuccess(payOrderReq);
        if (updateCount != 1) {
            throw new Exception("更新记录为0");
        }
        return updateCount;
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

}
