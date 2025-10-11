package chat.platform.plus.domain.trade.service.deliver;

public interface DeliverService {

    /**
     * 发货
     * @param userId 用户ID
     * @param goodsExpr 商品表达式
     */
    void deliver(String userId, Integer goodsExpr) throws Exception;
}
