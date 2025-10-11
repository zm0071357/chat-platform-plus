package chat.platform.plus.trigger.http;

import chat.platform.plus.api.ShoppingService;
import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.ShopCartEntity;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.domain.trade.service.goods.GoodsService;
import chat.platform.plus.domain.trade.service.pay.TradeService;
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
@SaCheckLogin
@RestController
@RequestMapping("/trade")
public class ShoppingController implements ShoppingService {

    @Resource
    private GoodsService goodsService;

    @Resource
    private TradeService tradeService;

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

    @PostMapping("/group_buy_notify")
    @Override
    public String groupBuyNotify(@RequestBody GroupBuyNotifyDTO groupBuyNotifyDTO) {
        if (StringUtils.isBlank(groupBuyNotifyDTO.getTeamId()) || groupBuyNotifyDTO.getOutTradeNoList() == null || groupBuyNotifyDTO.getOutTradeNoList().isEmpty()) {
            return "FAIL";
        }
        try {
            log.info("拼团营销服务 - 成团回调结算开始：{}", JSON.toJSONString(groupBuyNotifyDTO));
            tradeService.orderTeamComplete(groupBuyNotifyDTO.getTeamId(), groupBuyNotifyDTO.getOutTradeNoList());
            log.info("拼团营销服务 - 成团回调结算完成：{}", JSON.toJSONString(groupBuyNotifyDTO));
            return "SUCCESS";
        } catch (Exception e) {
            log.info("拼团营销服务 - 成团回调结算失败：{}", JSON.toJSONString(groupBuyNotifyDTO), e);
            return "FAIL";
        }
    }

}
