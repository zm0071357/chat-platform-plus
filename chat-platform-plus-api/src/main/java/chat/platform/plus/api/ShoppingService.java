package chat.platform.plus.api;

import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShoppingService {

    /**
     * 获取商品列表
     * @return
     */
    Response<List<GoodsResDTO>> getGoodsList();

    /**
     * 根据商品ID获取商品详情
     * @param goodsId 商品ID
     * @return
     */
    Response<GoodsDetailResDTO> getGoodsByID(String goodsId);

    /**
     * 创建订单 - 流水单
     * @param createPayOrderRequestDTO
     * @return
     */
    Response<CreatePayOrderResponseDTO> createPayOrder(CreatePayOrderRequestDTO createPayOrderRequestDTO);

    /**
     * 蓝兔支付 - 支付回调
     * 支付完成后，蓝兔支付会把相关支付结果和用户信息发送给商户
     * @param code 支付结果，枚举值：0：成功 1：失败
     * @param timestamp 时间戳
     * @param mchId 商户号
     * @param orderNo 系统订单号
     * @param orderId 商户订单号
     * @param payNo 微信支付订单号
     * @param orderPrice 金额
     * @param sign 签名
     * @param payChannel 支付渠道
     * @param tradeType 支付类型
     * @param successTime 支付完成时间
     * @param attach 附加数据
     * @param openid 支付者信息
     * @return
     */
    ResponseEntity<String> payNotify(
            String code,
            String timestamp,
            String mchId,
            String orderNo,
            String orderId,
            String payNo,
            String orderPrice,
            String sign,
            String payChannel,
            String tradeType,
            String successTime,
            String attach,
            String openid);

    /**
     * 拼团营销服务 - 成团结算回调
     * @param groupBuyNotifyDTO
     * @return
     */
    String groupBuyNotify(GroupBuyNotifyDTO groupBuyNotifyDTO);

}