package chat.platform.plus.domain.trade.adapter.repository;

public interface DeliverRepository {

    /**
     * 可调用次数发货
     * @param userId 用户ID
     * @param goodsExpr 商品表达式 - 可调用次数
     */
    void deliverCount(String userId, Integer goodsExpr) throws Exception;

    /**
     * VIP特权发货
     * @param userId 用户ID
     * @param goodsExpr 商品表达式 - VIP特权标识
     */
    void deliverVip(String userId, Integer goodsExpr) throws Exception;

    /**
     * 积分发货
     * @param userId 用户ID
     * @param goodsExpr 商品表达式
     */
    void deliverPoints(String userId, Integer goodsExpr) throws Exception;

}
