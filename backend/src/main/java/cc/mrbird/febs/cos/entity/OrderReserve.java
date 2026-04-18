package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 课程预约
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderReserve implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    private String code;

    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 价格
     */
    private BigDecimal totalPrice;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 课程订单ID
     */
    private Integer serviceId;

    /**
     * 预约时间
     */
    private String reserveDate;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String staffName;


}
