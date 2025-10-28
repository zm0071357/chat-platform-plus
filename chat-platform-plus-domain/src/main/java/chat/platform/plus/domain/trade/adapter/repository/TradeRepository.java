package chat.platform.plus.domain.trade.adapter.repository;

import chat.platform.plus.domain.trade.model.entity.*;

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
     * 保存预支付订单
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

    /**
     * 查询用户是否退单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return
     */
    RefundOrderEntity getRefundOrder(String userId, String orderId) throws Exception;

    /**
     * 获取订单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return
     */
    PayOrderEntity getPayOrder(String userId, String orderId) throws Exception;

    /**
     * 保存退单订单
     * @param refundOrderEntity
     */
    void saveRefundOrder(RefundOrderEntity refundOrderEntity);

    /**
     * 订单退款成功
     * @param refundOrderId 退单订单ID
     * @param refundTime 退单时间
     */
    void orderRefundSuccess(String refundOrderId, Date refundTime) throws Exception;

    /**
     * 获取未补偿完成的团长退单补偿任务
     * @return
     */
    List<HeaderCompensateTaskEntity> getUnCompleteHeaderRefundCompensateTaskList() throws Exception;

    /**
     * 保存团长退单补偿任务
     * @param userId 用户ID
     * @param teamId 拼团组队ID
     * @param teamStatus 拼团组队状态
     */
    void saveHeaderRefundCompensateTask(String userId, String teamId, Integer teamStatus) ;

    /**
     * 进行团长退单补偿
     * @param headerId
     * @param teamId
     * @param teamStatus
     */
    void headerRefundCompensate(String headerId, String teamId, Integer teamStatus) throws Exception;

    /**
     * 团长退单补偿失败
     * @param headerId
     * @param teamId
     * @param teamStatus
     */
    void headerRefundCompensateFail(String headerId, String teamId, Integer teamStatus) throws Exception;

    /**
     * 获取未回调退单单号集合
     * @return
     */
    List<String> getUnNotifyRefundOrderIdList();

    /**
     * 邀请返利
     * @param inviteUserIdList 邀请人ID集合
     */
    void inviteRebate(List<String> inviteUserIdList) throws Exception;
}