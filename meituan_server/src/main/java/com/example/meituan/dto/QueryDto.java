package com.example.meituan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryDto {
    private Integer pageNum;
    private Integer pageSize;
    private String query;
    private String type;
    private String businessDistrict;
    private Integer bottomPrice;
    private Integer topPrice;
}
