package com.example.meituan.controller;

import com.example.meituan.pojo.R;
import com.example.meituan.service.FoodDocService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author : [wangminan]
 * @description : [区划角度的controller]
 */
@RestController
public class DistrictController {

    @Resource
    private FoodDocService foodDocService;

    /**
     * 西安各行政区划餐饮商户数量对比
     * @return 西安各行政区划餐饮商户数量对比
     * @throws IOException IO异常
     */
    @GetMapping("/district/merchantNumber")
    public R getMerchantNumberByDistrict() throws IOException {
        return foodDocService.getMerchantNumberByDistrict();
    }

    /**
     * 西安各行政区划餐饮历史评价数量对比
     * @return 西安各行政区划餐饮历史评价数量对比
     * @throws IOException IO异常
     */
    @GetMapping("/district/flow")
    public R getFlowByDistrict() throws IOException {
        return foodDocService.getFlowByDistrict();
    }

    @GetMapping("/district/merchantType")
    public R getMerchantTypeByDistrict() throws IOException {
        return foodDocService.getMerchantTypeByDistrict();
    }

    /**
     * 西安各行政区划餐饮平均价格对比
     * @return 西安各行政区划餐饮平均价格对比
     * @throws IOException IO异常
     */
    @GetMapping("/district/avgPrice")
    public R getAvgPriceByDistrict() throws IOException {
        return foodDocService.getAvgPriceByDistrict();
    }
}
