package chat.platform.plus.domain.trade.adapter.repository;

import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface TradeRepository {

    /**
     * 获取商品集合
     * @return
     */
    List<GoodsEntity> getGoodsList();

    /**
     * 根据商品ID查询商品信息
     * @return
     */
    GoodsDetailEntity getGoodsByID(String goodsId);

    /**
     * 查询用户是否存在相同商品的未支付订单
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return
     */
    PayOrderEntity getUnPaidOrder(String userId, String goodsId) throws Exception;

    /**
     * 保存预支付
     * @param prePayOrderEntity
     * @return
     */
    void savePrePayOrder(PrePayOrderEntity prePayOrderEntity);

    /**
     * 查询短时间为待支付状态的订单ID集合
     * @return
     */
    List<String> getUnNotifyOrderIdList();

    /**
     * 更新订单状态为已支付
     * @param orderId 订单ID
     * @param payTime 支付时间
     * @return
     */
    void updateOrderStatusPaySuccess(String orderId, Date payTime) throws Exception;

    /**
     * 查询长时间为待支付状态的订单ID集合
     * @return
     */
    List<String> getTimeOutOrderIdList();

    /**
     * 更新订单状态为关单
     * @param orderId 订单ID
     */
    void updateOrderStatusClose(String orderId);

    /**
     * 根据订单ID获取订单
     * @param orderId 订单ID
     * @return
     */
    PayOrderEntity getUnPaidOrder(String orderId) throws Exception;

    /**
     * 更新订单状态为待支付
     * @param orderId 订单ID
     * @param payUrl 支付地址
     * @return
     */
    void updateOrderStatusPayWait(String orderId, String payUrl) throws Exception;

    /**
     * 更新订单状态为拼团完成
     * @param outTradeNoList 外部交易单号集合 - 订单ID集合
     */
    void updateOrderStatusTeamComplete(List<String> outTradeNoList) throws Exception;

    /**
     * 发货
     * @param orderId 订单ID
     */
    void deliverGoods(String orderId) throws Exception;

    /**
     * 发货
     * @param orderIdList 订单ID集合
     */
    void deliverGoods(List<String> orderIdList) throws Exception;

    /**
     * 更新订单的折扣价格和支付价格
     * @param orderId 订单ID
     * @param deductionPrice 折扣价格
     * @param payPrice 支付价格
     * @return
     */
    void updateOrderPrice(String orderId, BigDecimal deductionPrice, BigDecimal payPrice) throws Exception;

    /**
     * 结算 - 更新订单状态，根据订单类型进行回调/直接发货
     * @param orderId 订单ID
     * @param orderPayTime 订单支付时间
     */
    void settle(String orderId, Date orderPayTime) throws Exception;
}