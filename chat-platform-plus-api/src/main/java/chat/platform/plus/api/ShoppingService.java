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
     * 创建支付订单
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
     * @param teamCompleteNotifyDTO
     * @return
     */
    String teamCompleteNotify(TeamCompleteNotifyDTO teamCompleteNotifyDTO);

    /**
     * 创建退单订单
     * @param createRefundOrderRequestDTO
     * @return
     */
    Response<CreateRefundOrderResponseDTO> createRefundOrder(CreateRefundOrderRequestDTO createRefundOrderRequestDTO);

    /**
     * 蓝兔支付 - 退款回调
     * 退款完成后，蓝兔支付会把相关支付结果和用户信息发送给商户
     * @param code 支付结果，枚举值：0：成功 1：失败
     * @param timestamp 时间戳
     * @param mchId 商户号
     * @param orderNo 系统订单号
     * @param orderId 商户订单号
     * @param payNo 微信支付订单号
     * @param refundNo 系统退款单号
     * @param refundOrderId 商户退款单号
     * @param payChannel 退款渠道
     * @param refundOrderPrice 金额
     * @param sign 签名
     * @param successTime 退款完成时间
     * @return
     */
    ResponseEntity<String> refundNotify(
            String code,
            String timestamp,
            String mchId,
            String orderNo,
            String orderId,
            String payNo,
            String refundNo,
            String refundOrderId,
            String payChannel,
            String refundOrderPrice,
            String sign,
            String successTime
    );

    /**
     * 拼团营销服务 - 团长退单补偿回调
     * @param headerRefundNotifyDTO
     * @return
     */
    String headerRefundNotify(HeaderRefundNotifyDTO headerRefundNotifyDTO);

}