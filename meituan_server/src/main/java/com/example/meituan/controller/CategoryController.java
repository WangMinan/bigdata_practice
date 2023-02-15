package com.example.meituan.controller;

import com.example.meituan.pojo.R;
import com.example.meituan.service.CategoryService;
import org.elasticsearch.index.engine.Engine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author : [wangminan]
 * @description : [一句话描述该类的功能]
 */
@RestController
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/type/number")
    public R getNumber() throws IOException {
        return categoryService.getTotalShopByCategory();
    }

    @GetMapping("/type/avgPrice")
    public R getAvgPrice() throws IOException {
        return categoryService.getAvgPriceByCategory();
    }

    @GetMapping("/type/flow")
    public R getFlow(){
        return categoryService.getTotalCommentByCategory();
    }
}
