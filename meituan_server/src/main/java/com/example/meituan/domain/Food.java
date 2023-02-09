package com.example.meituan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @TableName food
 */
@TableName(value ="food")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food implements Serializable {
    /**
     * ID
     */
    @TableId(value = "poiId", type = IdType.INPUT)
    private String poiId;

    /**
     * 图标
     */
    @TableField("frontImg")
    private String frontImg;

    /**
     * 店铺名
     */
    private String title;

    /**
     * 类别 多个类别以逗号分隔
     */
    private String category;

    /**
     * 所在区
     */
    private String district;

    /**
     * 所在商圈
     */
    @TableField("businessDistrict")
    private String businessDistrict;

    /**
     * 平均评分
     */
    @TableField("avgScore")
    private long avgScore;

    /**
     * 总评论数
     */
    @TableField("allCommentNum")
    private Integer allCommentNum;

    /**
     * 平均价格
     */
    @TableField("avgPrice")
    private Integer avgPrice;

    private Double latitude;

    private Double longitude;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
