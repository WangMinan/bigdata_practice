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
public class SmartSuggestionDto {

    private String keyword;
    // 用户所在纬度
    private Double latitude;

    // 用户所在经度
    private Double longitude;
}
