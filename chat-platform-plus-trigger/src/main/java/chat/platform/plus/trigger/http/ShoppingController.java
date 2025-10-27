package chat.platform.plus.trigger.http;

import chat.platform.plus.api.ShoppingService;
import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.trade.model.entity.*;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.domain.trade.service.goods.GoodsService;
import chat.platform.plus.domain.trade.service.trade.TradeService;
import chat.platform.plus.types.enums.CommonEnum;
import cn.dev33.satoken.annotation.SaCheckLogin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.util.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.common.CodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/trade")
public class ShoppingController implements ShoppingService {

    @Resource
    private GoodsService goodsService;

    @Resource
    private TradeService tradeService;

    @SaCheckLogin
    @GetMapping("/goods_list")
    @Override
    public Response<List<GoodsResDTO>> getGoodsList() {
        List<GoodsEntity> goodsList = goodsService.getGoodsList();
        List<GoodsResDTO> goodsResDTOList = new ArrayList<>();
        if (goodsList != null && !goodsList.isEmpty()) {
            for (GoodsEntity goodsEntity : goodsList) {
                goodsResDTOList.add(GoodsResDTO.builder()
                        .goodsId(goodsEntity.getGoodsId())
                        .goodsName(goodsEntity.getGoodsName())
                        .goodsPrice(goodsEntity.getGoodsPrice())
                        .build());
            }
        }
        return Response.<List<GoodsResDTO>>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(goodsResDTOList)
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @SaCheckLogin
    @GetMapping("/goods_detail/{goodsId}")
    @Override
    public Response<GoodsDetailResDTO> getGoodsByID(@PathVariable("goodsId") String goodsId) {
        if (StringUtils.isBlank(goodsId)) {
            return Response.<GoodsDetailResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        GoodsDetailEntity goodsDetailEntity = goodsService.getGoodsByID(goodsId);
        if (goodsDetailEntity == null) {
            return Response.<GoodsDetailResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        return Response.<GoodsDetailResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(GoodsDetailResDTO.builder()
                        .goodsId(goodsId)
                        .goodsName(goodsDetailEntity.getGoodsName())
                        .goodsDesc(goodsDetailEntity.getGoodsDesc())
                        .goodsPrice(goodsDetailEntity.getGoodsPrice())
                        .goodsType(goodsDetailEntity.getGoodsType().getType())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @SaCheckLogin
    @PostMapping("/create_order")
    @Override
    public Response<CreatePayOrderResponseDTO> createPayOrder(@RequestBody CreatePayOrderRequestDTO createPayOrderRequestDTO) {
        try {
            // 参数校验
            if (StringUtils.isBlank(createPayOrderRequestDTO.getGoodsId()) || StringUtils.isBlank(createPayOrderRequestDTO.getUserId()) ||
                    createPayOrderRequestDTO.getActivityId() == null || createPayOrderRequestDTO.getOrderType() == null) {
                return Response.<CreatePayOrderResponseDTO>builder()
                        .code(CommonEnum.LACK_PARAM.getCode())
                        .info(CommonEnum.LACK_PARAM.getInfo())
                        .build();
            }
            log.info("创建支付订单开始，用户ID：{}，商品ID：{}", createPayOrderRequestDTO.getUserId(), createPayOrderRequestDTO.getGoodsId());
            // 创建支付订单
            PayOrderEntity payOrderEntity = tradeService.createOrder(ShopCartEntity.builder()
                            .userId(createPayOrderRequestDTO.getUserId())
                            .teamId(createPayOrderRequestDTO.getTeamId())
                            .goodsId(createPayOrderRequestDTO.getGoodsId())
                            .activityId(createPayOrderRequestDTO.getActivityId())
                            .orderTypesEnum(OrderTypesEnum.get(createPayOrderRequestDTO.getOrderType()))
                            .build());
            log.info("创建支付订单完成，用户ID：{}，商品ID：{}，订单信息：{}", createPayOrderRequestDTO.getUserId(), createPayOrderRequestDTO.getGoodsId(), JSON.toJSONString(payOrderEntity));
            return Response.<CreatePayOrderResponseDTO>builder()
                    .code(CommonEnum.SUCCESS.getCode())
                    .data(CreatePayOrderResponseDTO.builder()
                            .userId(payOrderEntity.getUserId())
                            .goodsId(payOrderEntity.getGoodsId())
                            .orderId(payOrderEntity.getOrderId())
                            .orderPrice(payOrderEntity.getOrderPrice())
                            .originalPrice(payOrderEntity.getOriginalPrice())
                            .deductionPrice(payOrderEntity.getDeductionPrice())
                            .payPrice(payOrderEntity.getPayPrice())
                            .orderCreateTime(payOrderEntity.getOrderCreateTime())
                            .payUrl(payOrderEntity.getPayUrl())
                            .build())
                    .info(CommonEnum.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            log.info("创建支付订单失败，用户ID：{}，商品ID：{}", createPayOrderRequestDTO.getUserId(), createPayOrderRequestDTO.getGoodsId(), e);
            return Response.<CreatePayOrderResponseDTO>builder()
                    .code(CommonEnum.UNKNOWN_ERROR.getCode())
                    .info(CommonEnum.UNKNOWN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("/pay_notify")
    @Override
    public ResponseEntity<String> payNotify(@RequestParam("code") String code,
                                            @RequestParam("timestamp") String timestamp,
                                            @RequestParam("mch_id") String mchId,
                                            @RequestParam("order_no") String orderNo,
                                            @RequestParam("out_trade_no") String orderId,
                                            @RequestParam("pay_no") String payNo,
                                            @RequestParam("total_fee") String orderPrice,
                                            @RequestParam("sign") String sign,
                                            @RequestParam("pay_channel") String payChannel,
                                            @RequestParam("trade_type") String tradeType,
                                            @RequestParam("success_time") String successTime,
                                            @RequestParam("attach") String attach,
                                            @RequestParam("openid") String openid) {
        try {
            log.info("支付回调，请求参数：{} {} {} {} {} {} {} {} {} {} {} {} {}",
                    code, timestamp, mchId, orderNo, orderId, payNo, orderPrice,
                    sign, payChannel, tradeType, successTime, attach, openid);
            // TODO：签名验签
            // 支付回调结果成功
            if (code.equals(CodeEnum.SUCCESS.getCode()) && successTime != null) {
                log.info("订单支付成功，订单ID：{}", orderNo);
                tradeService.orderPaySuccess(orderId, DateUtils.parseDate(successTime, "yyyy-MM-dd HH:mm:ss"));
            }
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/group_buy/notify")
    @Override
    public String teamCompleteNotify(@RequestBody TeamCompleteNotifyDTO teamCompleteNotifyDTO) {
        if (StringUtils.isBlank(teamCompleteNotifyDTO.getTeamId()) || teamCompleteNotifyDTO.getOutTradeNoList() == null ||
                teamCompleteNotifyDTO.getOutTradeNoList().isEmpty()) {
            return "FAIL";
        }
        try {
            log.info("拼团营销服务 - 成团回调结算开始：{}", JSON.toJSONString(teamCompleteNotifyDTO));
            tradeService.orderTeamComplete(teamCompleteNotifyDTO.getTeamId(), teamCompleteNotifyDTO.getOutTradeNoList());
            log.info("拼团营销服务 - 成团回调结算完成：{}", JSON.toJSONString(teamCompleteNotifyDTO));
            return "SUCCESS";
        } catch (Exception e) {
            log.info("拼团营销服务 - 成团回调结算失败：{}", JSON.toJSONString(teamCompleteNotifyDTO), e);
            return "FAIL";
        }
    }

    @SaCheckLogin
    @PostMapping("/create/refund_order")
    @Override
    public Response<CreateRefundOrderResponseDTO> createRefundOrder(@RequestBody CreateRefundOrderRequestDTO createRefundOrderRequestDTO) {
        try {
            // 参数校验
            if (StringUtils.isBlank(createRefundOrderRequestDTO.getUserId()) || StringUtils.isBlank(createRefundOrderRequestDTO.getOrderId())) {
                return Response.<CreateRefundOrderResponseDTO>builder()
                        .code(CommonEnum.LACK_PARAM.getCode())
                        .info(CommonEnum.LACK_PARAM.getInfo())
                        .build();
            }
            log.info("创建退单订单开始，用户ID：{}，支付订单ID：{}", createRefundOrderRequestDTO.getUserId(), createRefundOrderRequestDTO.getOrderId());
            // 创建退单订单
            RefundResEntity refundResEntity = tradeService.createRefundOrder(PreRefundOrderEntity.builder()
                    .userId(createRefundOrderRequestDTO.getUserId())
                    .orderId(createRefundOrderRequestDTO.getOrderId())
                    .desc(createRefundOrderRequestDTO.getDesc())
                    .build());
            log.info("创建退单订单完成，用户ID：{}，支付订单ID：{}，退单订单信息：{}", createRefundOrderRequestDTO.getUserId(), createRefundOrderRequestDTO.getOrderId(), JSON.toJSONString(refundResEntity));
            return Response.<CreateRefundOrderResponseDTO>builder()
                    .code(CommonEnum.SUCCESS.getCode())
                    .data(CreateRefundOrderResponseDTO.builder()
                            .userId(refundResEntity.getUserId())
                            .orderId(refundResEntity.getOrderId())
                            .refundOrderId(refundResEntity.getRefundOrderId())
                            .refundOrderCreateTime(refundResEntity.getRefundOrderCreateTime())
                            .status(refundResEntity.getRefundOrderStatusEnum().getStatus())
                            .info(refundResEntity.getInfo())
                            .build())
                    .info(CommonEnum.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            log.info("创建退单订单失败，用户ID：{}，支付订单ID：{}", createRefundOrderRequestDTO.getUserId(), createRefundOrderRequestDTO.getOrderId(), e);
            return Response.<CreateRefundOrderResponseDTO>builder()
                    .code(CommonEnum.UNKNOWN_ERROR.getCode())
                    .info(CommonEnum.UNKNOWN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("/refund_notify")
    @Override
    public ResponseEntity<String> refundNotify(@RequestParam("code") String code,
                                               @RequestParam("timestamp") String timestamp,
                                               @RequestParam("mch_id") String mchId,
                                               @RequestParam("order_no") String orderNo,
                                               @RequestParam("out_trade_no") String orderId,
                                               @RequestParam("pay_no") String payNo,
                                               @RequestParam("refund_no") String refundNo,
                                               @RequestParam("out_refund_no") String refundOrderId,
                                               @RequestParam("pay_channel") String payChannel,
                                               @RequestParam("refund_fee") String refundOrderPrice,
                                               @RequestParam("sign") String sign,
                                               @RequestParam("success_time") String successTime) {
        try {
            log.info("退款回调，请求参数：{} {} {} {} {} {} {} {} {} {} {} {}",
                    code, timestamp, mchId, orderNo, orderId, payNo, refundNo,
                    refundOrderId, payChannel, refundOrderPrice, sign, successTime);
            // TODO：签名验签
            // 退款回调结果成功
            if (code.equals(CodeEnum.SUCCESS.getCode()) && successTime != null) {
                log.info("订单退款成功，退款订单ID：{}", refundNo);
                tradeService.orderRefundSuccess(refundNo, DateUtils.parseDate(successTime, "yyyy-MM-dd HH:mm:ss"));
            }
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("FAIL", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/group_buy/header_refund_notify")
    @Override
    public String headerRefundNotify(@RequestBody HeaderRefundNotifyDTO headerRefundNotifyDTO) {
        if (StringUtils.isBlank(headerRefundNotifyDTO.getUserId()) || StringUtils.isBlank(headerRefundNotifyDTO.getTeamId()) ||
                headerRefundNotifyDTO.getTeamStatus() == null) {
            return "FAIL";
        }
        try {
            log.info("拼团营销服务 - 团长退单补偿回调开始：{}", JSON.toJSONString(headerRefundNotifyDTO));
            tradeService.headerRefundCompensate(headerRefundNotifyDTO.getUserId(), headerRefundNotifyDTO.getTeamId(), headerRefundNotifyDTO.getTeamStatus());
            log.info("拼团营销服务 - 团长退单补偿回调完成：{}", JSON.toJSONString(headerRefundNotifyDTO));
            return "SUCCESS";
        } catch (Exception e) {
            log.info("拼团营销服务 - 团长退单补偿回调失败：{}", JSON.toJSONString(headerRefundNotifyDTO), e);
            return "FAIL";
        }
    }

}
