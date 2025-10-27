package chat.platform.plus.infrastructure.dao;

import chat.platform.plus.infrastructure.dao.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PayOrderDao {

    /**
     * 查询未支付订单
     * @param payOrderReq
     * @return
     */
    PayOrder getUnpaidOrder(PayOrder payOrderReq);

    /**
     * 插入订单
     * @param payOrder
     */
    void insert(PayOrder payOrder);

    /**
     * 查询短时间为待支付状态的订单ID集合
     * @return
     */
    List<String> getUnNotifyOrderIdList();

    /**
     * 更新订单状态为已支付
     * @param payOrderReq
     * @return
     */
    Integer updateOrderStatusPaySuccess(PayOrder payOrderReq);

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
     * 查询未支付订单
     * @param orderId 订单ID
     * @return
     */
    PayOrder getUnPaidOrder(String orderId);

    /**
     * 更新订单状态为待支付
     * @param payOrderReq
     * @return
     */
    Integer updateOrderStatusPayWait(PayOrder payOrderReq);

    /**
     * 更新订单状态为拼团完成
     * @param outTradeNoList 外部交易单号集合 - 订单ID集合
     * @return
     */
    Integer updateOrderStatusTeamComplete(@Param("outTradeNoList") List<String> outTradeNoList);

    /**
     * 根据订单ID获取未发货订单
     * @param orderId 订单ID
     * @return
     */
    PayOrder getUnDeliverGoodsOrder(String orderId);

    /**
     * 更新订单状态为交易完成
     * @param orderId 订单ID
     * @return
     */
    Integer updateOrderStatusDealDone(String orderId);

    /**
     * 更新订单折扣价格和支付价格
     * @param payOrderReq
     * @return
     */
    Integer updateOrderPrice(PayOrder payOrderReq);

    /**
     * 获取支付订单
     * @param payOrderReq
     * @return
     */
    PayOrder getPayOrder(PayOrder payOrderReq);
}
