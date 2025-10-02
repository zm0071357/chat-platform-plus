package chat.platform.plus.infrastructure.dao;

import chat.platform.plus.infrastructure.dao.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;

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
}
