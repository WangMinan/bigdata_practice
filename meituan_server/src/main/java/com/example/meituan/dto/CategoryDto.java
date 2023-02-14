package com.example.meituan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : [林昊辰]
 * @description : [一句话描述该类的功能]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private String category;

    private Integer count;
}
