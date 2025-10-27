package chat.platform.plus.infrastructure.dao;

import chat.platform.plus.infrastructure.dao.po.RefundOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RefundOrderDao {

    /**
     * 新增退单订单
     * @param refundOrder
     */
    void insert(RefundOrder refundOrder);

    /**
     * 获取退单订单 - 根据用户ID和支付订单ID
     * @param refundOrderReq
     * @return
     */
    RefundOrder getRefundOrderByUserIdWithPayOrderId(RefundOrder refundOrderReq);

    /**
     * 获取退单订单 - 根据退单订单ID
     * @param refundOrderReq
     * @return
     */
    RefundOrder getRefundOrderByRefundOrderId(RefundOrder refundOrderReq);

    /**
     * 更新退单订单状态为退款成功
     * @param refundOrderReq
     * @return
     */
    Integer updateOrderRefundSuccess(RefundOrder refundOrderReq);

    /**
     * 获取未回调的退单订单ID集合
     * @return
     */
    List<String> getUnNotifyRefundOrderIdList();
}
