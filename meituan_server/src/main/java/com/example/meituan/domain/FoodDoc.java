package com.example.meituan.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author : [wangminan]
 * @description : [elasticSearch中的food文档实体]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodDoc {
    /**
     * ID
     */
    private String poiId;

    /**
     * 图标
     */
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
    private String businessDistrict;

    /**
     * 平均评分
     */
    private Integer avgScore;

    /**
     * 总评论数
     */
    private Integer allCommentNum;

    /**
     * 平均价格
     */
    private Integer avgPrice;

    /**
     * 自动补全索引
     */
    private List<String> suggestion;

    private static final long serialVersionUID = 1L;

    public FoodDoc(Food food){
        this.poiId = food.getPoiId();
        this.frontImg = food.getFrontImg();
        this.title = food.getTitle();

        if(food.getCategory() == null){
            this.category = "";
        } else {
            this.category = food.getCategory();
        }

        if(food.getDistrict() == null){
            this.district = "";
        } else {
            this.district = food.getDistrict();
        }

        if(food.getBusinessDistrict() == null){
            this.businessDistrict = "";
        } else {
            this.businessDistrict = food.getBusinessDistrict();
        }

        this.avgScore = food.getAvgScore();
        this.allCommentNum = food.getAllCommentNum();
        this.avgPrice = food.getAvgPrice();
        this.suggestion = new ArrayList<>();

        if(!Objects.equals(this.getCategory(), "")) {
            if (this.getCategory().contains(",")) {
                this.suggestion.addAll(Arrays.asList(food.getCategory().split(",")));
            } else {
                this.suggestion.add(food.getCategory());
            }
        }

        if(!Objects.equals(this.getBusinessDistrict(), "")) {
            if(this.getBusinessDistrict().contains("_")){
                this.suggestion.addAll(Arrays.asList(food.getBusinessDistrict().split("_")));
            } else {
                this.suggestion.add(food.getBusinessDistrict());
            }
        }

        this.suggestion.add(this.title);
    }
}
