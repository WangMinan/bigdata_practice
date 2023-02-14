package com.example.meituan.dto;

import com.example.meituan.domain.FoodDoc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult {
    private Long total;
    private List<FoodDoc> merchants;
}
