package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 健身设施管理
 *
 * @author FanK fan1ke2ke@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 健身设施编号
     */
    private String code;

    /**
     * 健身设施名称
     */
    private String deviceName;

    /**
     * 负责人
     */
    private String deviceCharge;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 健身设施备注
     */
    private String content;

    /**
     * 健身设施图片
     */
    private String images;

    /**
     * 健身设施状态（0.废弃 1.正常 2.维修保养中）
     */
    private String status;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 健身设施类型
     */
    private String type;

    /**
     * 健身设施地址
     */
    private String address;


}
