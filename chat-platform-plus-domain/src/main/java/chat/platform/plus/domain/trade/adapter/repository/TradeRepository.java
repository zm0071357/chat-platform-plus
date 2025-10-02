package chat.platform.plus.domain.trade.adapter.repository;

import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;

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
    Integer updateOrderStatusPaySuccess(String orderId, Date payTime) throws Exception;

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
}
