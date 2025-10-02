package chat.platform.plus.infrastructure.dao.po;

import chat.platform.plus.domain.trade.model.valobj.GoodsTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Goods {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商品价格
     */
    private BigDecimal goodsPrice;

    /**
     * 商品类型
     */
    private Integer goodsType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
