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
 * 治疗过程
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ServiceReserveInfo implements Serializable {

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
     * 作业类型
     */
    private String type;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 价格
     */
    private BigDecimal totalPrice;

    /**
     * 详细位置
     */
    private String address;

    /**
     * 问题照片
     */
    private String images;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 经度
     */
    private BigDecimal latitude;

    /**
     * 状态（0.未支付 1.治疗中 2.已结束）
     */
    private String status;

    /**
     * 接单人员
     */
    private Integer workUserId;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 康复内容
     */
    private String fixContent;

    @TableField(exist = false)
    private String workName;

    @TableField(exist = false)
    private String userName;
}
