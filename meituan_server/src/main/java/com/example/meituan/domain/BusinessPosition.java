package com.example.meituan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName business_position
 */
@TableName(value ="business_position")
@Data
public class BusinessPosition implements Serializable {
    /**
     * 西安各个商圈地址
     */
    @TableId
    private String address;

    /**
     * 地址纬度
     */
    private Double latitude;

    /**
     * 地址经度
     */
    private Double longitude;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
